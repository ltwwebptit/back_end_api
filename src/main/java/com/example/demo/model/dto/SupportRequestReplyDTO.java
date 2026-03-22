package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportRequestReplyDTO {
    @NotBlank(message = "Nội dung trả lời không được để trống")
    private String replyMessage;
}
