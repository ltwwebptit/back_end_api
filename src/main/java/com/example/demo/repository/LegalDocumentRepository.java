package com.example.demo.repository;

import com.example.demo.entity.LegalDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocumentEntity, Integer> {
}
