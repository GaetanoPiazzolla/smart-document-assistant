package gae.piaz.sda.controller.dto;

import lombok.Data;

@Data
public class ChatMessage {

    private String message;
    private String chatId;
    private Boolean isResponse = false;

}
