package smart.document.assistant.app.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import smart.document.assistant.app.client.ChatMessageBody;
import smart.document.assistant.app.springai.SpringAiAssistant;

@RestController
@CrossOrigin
public class AssistantController {

    private final SpringAiAssistant springAiAssistant;

    public AssistantController(SpringAiAssistant springAiAssistant) {
        this.springAiAssistant = springAiAssistant;
    }

    // curl -X POST -H "Content-Type: application/json" -d '{"chatId":"1", "message":"Hello"}' http://localhost:8080/chat
    @PostMapping("chat")
    public Flux<String> chat(@RequestBody ChatMessageBody chatMessageBody) {
        return springAiAssistant.chat(chatMessageBody.getChatId(), chatMessageBody.getMessage());
    }

}
