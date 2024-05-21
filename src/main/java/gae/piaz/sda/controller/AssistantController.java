package gae.piaz.sda.controller;

import gae.piaz.sda.controller.dto.ChatMessageBody;
import gae.piaz.sda.springai.SpringAiAssistant;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin
@RequestMapping("api/v1/assistant")
public class AssistantController {

    private final SpringAiAssistant springAiAssistant;

    public AssistantController(SpringAiAssistant springAiAssistant) {
        this.springAiAssistant = springAiAssistant;
    }

    // curl -X POST -H "Content-Type: application/json" -d '{"chatId":"1", "message":"Hello"}'
    // http://localhost:8080/chat
    @PostMapping("chat")
    public Flux<String> chat(@RequestBody ChatMessageBody chatMessageBody) {
        return springAiAssistant.chat(chatMessageBody.getChatId(), chatMessageBody.getMessage());
    }
}
