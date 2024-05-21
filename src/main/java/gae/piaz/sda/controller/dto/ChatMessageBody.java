package gae.piaz.sda.controller.dto;

import lombok.Data;

@Data
public class ChatMessageBody {

    private String message;
    private String chatId;

}
