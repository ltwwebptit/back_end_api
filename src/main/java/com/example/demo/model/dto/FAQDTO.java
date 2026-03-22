package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FAQDTO {
    private Integer id;

    @NotBlank(message = "question is not blank")
    private String question;

    @NotBlank(message = "answer is not blank")
    private String answer;
}
