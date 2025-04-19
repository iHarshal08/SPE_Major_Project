package iiitb.messagingservice.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String to;
    private String message;
}
