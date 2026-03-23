package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "legal_documents")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LegalDocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String type; 

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name ="issuing_agency",nullable = false)
    private String issuingAgency;

    @Column(name ="issue_date",nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "status", nullable = false)
    private Boolean status = true;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private LegalCategoriesEntity category;
}
