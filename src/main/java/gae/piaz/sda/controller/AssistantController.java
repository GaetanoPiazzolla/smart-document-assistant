package gae.piaz.sda.controller;

import gae.piaz.sda.controller.dto.ChatMessage;
import gae.piaz.sda.springai.SpringAiAssistant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("api/v1/assistant")
public class AssistantController {

    private final SpringAiAssistant springAiAssistant;

    public AssistantController(SpringAiAssistant springAiAssistant) {
        this.springAiAssistant = springAiAssistant;
    }

    // TODO we should return flux!
    @PostMapping("chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody ChatMessage chatMessage) {
        Flux<String> msg = springAiAssistant.chat(chatMessage.getChatId(), chatMessage.getMessage());
        Mono<Object> mono = msg.collectList().map(list -> String.join(" ", list));
        String result = mono.block().toString();

        ChatMessage response = new ChatMessage();
        response.setMessage(result);
        response.setChatId(chatMessage.getChatId());
        response.setIsResponse(true);
        return ResponseEntity.ok(response);
    }
}
