package gae.piaz.sda.springai;

import gae.piaz.sda.service.FindDocumentsService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author Christian Tzolov @Author Gaetano Piazzolla
 */
@Service
public class SpringAiAssistant {

    private static final Logger logger = LoggerFactory.getLogger(SpringAiAssistant.class);

    private static final int CHAT_HISTORY_WINDOW_SIZE = 40;

    @Value("classpath:/spring-ai/system-qa.st")
    private Resource systemPrompt;

    @Value("${sda.pgvector.similarity_threshold}")
    double similarityThreshold;

    @Value("${sda.pgvector.k_nearest_neighbors}")
    int kNearestNeighbors;

    @Autowired private StreamingChatModel chatModel;

    @Autowired private VectorStore vectorStore;

    @Autowired private ChatHistory chatHistory;

    @Autowired private FindDocumentsService findDocumentsService;

    @Autowired private Environment environment;

    private FunctionCallingOptions promptOptions;

    @PostConstruct
    public void init() {
        // use OpenAi only if spring.ai.openai.api-key is set
        if (!StringUtils.isBlank(environment.getProperty("spring.ai.openai.api-key"))) {
            promptOptions =
                    OpenAiChatOptions.builder()
                            .withFunctionCallbacks(
                                    List.of(
                                            FunctionCallbackWrapper.builder(findDocumentsService)
                                                    .withName("FindDocumentsService")
                                                    .withDescription(
                                                            "Retrieve the number of occurrences of documents based on the provided criteria.")
                                                    .withResponseConverter(
                                                            (response) ->
                                                                    "" + response.occurrences())
                                                    .build()))
                            .build();
        } else {
            logger.error("no other AI model is available!");
            throw new IllegalStateException("spring.ai.openai.api-key is not set!");
        }
    }

    public Flux<String> chat(
            String chatId, String userMessageContent, boolean isFunctionCallEnabled) {

        // Retrieve related documents to query
        SearchRequest request =
                SearchRequest.query(userMessageContent)
                        .withSimilarityThreshold(similarityThreshold)
                        .withTopK(kNearestNeighbors);

        List<Document> similarDocuments = this.vectorStore.similaritySearch(request);

        Message systemMessage =
                getSystemMessage(
                        similarDocuments,
                        this.chatHistory.getLastN(chatId, CHAT_HISTORY_WINDOW_SIZE));
        logger.debug("System Message: {}", systemMessage.getContent());

        UserMessage userMessage = new UserMessage(userMessageContent);

        this.chatHistory.addMessage(chatId, userMessage);

        OpenAiChatOptions openAIprompt = (OpenAiChatOptions) promptOptions;

        /*
         * "none" means the model will not call any function
         * "auto" means the model can pick between generating a message or calling a function
         */
        if (isFunctionCallEnabled) {
            openAIprompt.setToolChoice(OpenAiApi.ChatCompletionRequest.ToolChoiceBuilder.AUTO);
        } else {
            openAIprompt.setToolChoice(OpenAiApi.ChatCompletionRequest.ToolChoiceBuilder.NONE);
        }
        // Ask the AI model
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), openAIprompt);

        return this.chatModel.stream(prompt)
                .map(
                        (ChatResponse chatResponse) -> {
                            if (!isValidResponse(chatResponse)) {
                                logger.warn("ChatResponse or the result output is null!");
                                return "";
                            }

                            AssistantMessage assistantMessage =
                                    chatResponse.getResult().getOutput();

                            this.chatHistory.addMessage(chatId, assistantMessage);

                            return (assistantMessage.getContent() != null)
                                    ? assistantMessage.getContent()
                                    : "";
                        });
    }

    private boolean isValidResponse(ChatResponse chatResponse) {
        return chatResponse != null
                && chatResponse.getResult() != null
                && chatResponse.getResult().getOutput() != null;
    }

    private Message getSystemMessage(
            List<Document> similarDocuments, List<Message> conversationHistory) {

        String history =
                conversationHistory.stream()
                        .map(m -> m.getMessageType().name().toLowerCase() + ": " + m.getContent())
                        .collect(Collectors.joining(System.lineSeparator()));

        String documents =
                similarDocuments.stream()
                        .map(Document::getContent)
                        .collect(Collectors.joining(System.lineSeparator()));

        // Needs to be created on each call as it is not thread safe.
        return new SystemPromptTemplate(this.systemPrompt)
                .createMessage(
                        Map.of(
                                "documents", documents,
                                "history", history));
    }
}
