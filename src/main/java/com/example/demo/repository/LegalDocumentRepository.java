package com.example.demo.repository;

import com.example.demo.entity.LegalDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocumentEntity, Integer> {

    @Modifying
    @Query("UPDATE LegalDocumentEntity l SET l.count = COALESCE(l.count, 0) + 1 WHERE l.id = :id")
    void incrementViewCount(@Param("id") Integer id);

    @Query("SELECT new com.example.demo.model.dto.LegalDocumentDTO(l.id, l.title, l.link, l.type, l.issuingAgency, l.issueDate, l.status, l.category.id) " +
           "FROM LegalDocumentEntity l WHERE " +
           "(:keyword IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:type IS NULL OR l.type = :type) AND " +
           "(:agency IS NULL OR l.issuingAgency = :agency) AND " +
           "(:year IS NULL OR YEAR(l.issueDate) = :year)")
    Page<com.example.demo.model.dto.LegalDocumentDTO> searchDocuments(
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("agency") String agency,
            @Param("year") Integer year,
            Pageable pageable
    );
}
