package gae.piaz.sda.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {
    private String message;
    private String chatId;
    private Boolean isResponse;
    private boolean withFunctionCall;
}
