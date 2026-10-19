package com.example.demo.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String content;
    private ClientDTO sender;
    private LocalDateTime sentAt;
    private String status;
}
