package gae.piaz.sda.controller;

import gae.piaz.sda.controller.dto.ChatMessage;
import gae.piaz.sda.springai.SpringAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@RequestMapping("api/v1/assistant")
public class AssistantController {

    @Autowired private SpringAiAssistant springAiAssistant;

    // TODO we should return flux!
    @PostMapping("chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody ChatMessage chatMessage) {
        Flux<String> msg =
                springAiAssistant.chat(
                        chatMessage.getChatId(),
                        chatMessage.getMessage(),
                        chatMessage.isWithFunctionCall());

        Mono<String> mono = msg.collectList().map(list -> String.join(" ", list));

        ChatMessage response =
                ChatMessage.builder()
                        .isResponse(true)
                        .chatId(chatMessage.getChatId())
                        .message(mono.block())
                        .build();

        return ResponseEntity.ok(response);
    }
}
