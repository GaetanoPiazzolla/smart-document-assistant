package gae.piaz.sda.controller;

import gae.piaz.sda.controller.dto.ChatMessage;
import gae.piaz.sda.springai.SpringAiAssistant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/assistant")
public class AssistantController {

    private final SpringAiAssistant springAiAssistant;

    public AssistantController(SpringAiAssistant springAiAssistant) {
        this.springAiAssistant = springAiAssistant;
    }

    @PostMapping("chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody ChatMessage chatMessage) {
        String msg = springAiAssistant.chat(chatMessage.getChatId(), chatMessage.getMessage());
        ChatMessage response = new ChatMessage();
        response.setMessage(msg);
        response.setChatId(chatMessage.getChatId());
        response.setIsResponse(true);
        return ResponseEntity.ok(response);
    }
}
