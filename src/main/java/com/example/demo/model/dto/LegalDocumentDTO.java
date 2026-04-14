package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LegalDocumentDTO {

    public LegalDocumentDTO(Integer id, String title, String link, String type, String issuingAgency, LocalDateTime issueDate, Boolean status, Integer categoryId) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.type = type;
        this.issuingAgency = issuingAgency;
        this.issueDate = issueDate;
        this.status = status;
        this.categoryId = categoryId;
    }
    private Integer id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private String link;

    @NotBlank(message = "Type cannot be blank")
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @NotBlank(message = "Issuing agency cannot be blank")
    private String issuingAgency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime issueDate;

    private Boolean status;
    private  Integer count = 0;
    @NotNull(message = "Category ID cannot be null")
    private Integer categoryId;
}
