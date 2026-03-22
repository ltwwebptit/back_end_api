package com.example.demo.service;

import com.example.demo.model.dto.LegalDocumentDTO;
import java.util.List;

public interface LegalDocumentService {
    LegalDocumentDTO create(LegalDocumentDTO dto);
    LegalDocumentDTO update(LegalDocumentDTO dto);
    void delete(Integer id);
    LegalDocumentDTO findById(Integer id);
    List<LegalDocumentDTO> findAll();
}
