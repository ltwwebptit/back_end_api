package com.example.demo.service;

import com.example.demo.model.dto.LegalDocumentDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LegalDocumentService {
    LegalDocumentDTO create(LegalDocumentDTO dto);
    LegalDocumentDTO update(LegalDocumentDTO dto);
    void delete(Integer id);
    LegalDocumentDTO findById(Integer id);
    List<LegalDocumentDTO> findAll();
    List<LegalDocumentDTO> findMaxCount(List<LegalDocumentDTO> dtos);
    Page<LegalDocumentDTO> searchDocuments(String keyword, String type, String agency, Integer year, int page, int size);
}
