package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(columnDefinition = "TEXT")
    private String link;

    @Column(nullable = true)
    private String type; 

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name ="issuing_agency", nullable = true)
    private String issuingAgency;

    @Column(name ="issue_date", nullable = true)
    private LocalDateTime issueDate;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "count",nullable = false)
    private Integer count = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private LegalCategoriesEntity category;
}
