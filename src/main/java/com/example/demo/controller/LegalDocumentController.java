package com.example.demo.controller;

import com.example.demo.model.dto.LegalDocumentDTO;
import com.example.demo.service.LegalDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legal-documents")
@RequiredArgsConstructor
@Slf4j
public class LegalDocumentController {

    private final LegalDocumentService service;

    @PostMapping
    public ResponseEntity<LegalDocumentDTO> createDocument(@Valid @RequestBody LegalDocumentDTO dto) {
        log.info("Received request to create legal document: {}", dto.getTitle());
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (Exception e) {
            log.error("Exception during createDocument: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalDocumentDTO> updateDocument(@PathVariable Integer id, @Valid @RequestBody LegalDocumentDTO dto) {
        log.info("Received request to update legal document id: {}", id);
        dto.setId(id);
        try {
            return ResponseEntity.ok(service.update(dto));
        } catch (Exception e) {
            log.error("Exception during updateDocument: ", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Integer id) {
        log.info("Received request to delete legal document id: {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Exception during deleteDocument: ", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalDocumentDTO> getDocumentById(@PathVariable Integer id) {
        log.info("Received request to fetch legal document id: {}", id);
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            log.error("Exception during getDocumentById: ", e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<LegalDocumentDTO>> getAllDocuments() {
        log.info("Received request to fetch all legal documents");
        try {
            return ResponseEntity.ok(service.findAll());
        } catch (Exception e) {
            log.error("Exception during getAllDocuments: ", e);
            throw e;
        }
    }
}
