package com.example.demo.controller;

import com.example.demo.model.dto.LegalDocumentDTO;
import com.example.demo.service.LegalDocumentService;
import com.example.demo.service.PDFService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/legal-documents")
@RequiredArgsConstructor
@Slf4j
public class LegalDocumentController {

    private final LegalDocumentService service;
    private final PDFService pdfService;

    @PostMapping
    public ResponseEntity<LegalDocumentDTO> createDocument(@Valid @RequestBody LegalDocumentDTO dto) {
        log.info("Received request to create legal document: {}", dto.getTitle());
        try {
            LegalDocumentDTO saved = service.create(dto);
            byte[] pdfBytes = pdfService.genaratePdf(saved);
            String fileName = "LegalDoc_" + saved.getId() + ".pdf";
            pdfService.sendPdfToAnotherService(pdfBytes, fileName);

            log.info("Quy trình hoàn tất cho tài liệu ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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

    @GetMapping("/getMaxcount")
    public ResponseEntity<List<LegalDocumentDTO>> getMaxCount() {
        log.info("Received request to fetch most viewed legal document");
        try {
            List<LegalDocumentDTO> allDocuments = service.findAll();
            List<LegalDocumentDTO> maxcount = service.findMaxCount(allDocuments);
            return ResponseEntity.ok(maxcount);
        } catch (Exception e) {
            log.error("Exception during getMaxCount: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<LegalDocumentDTO>> searchDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String agency,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Search request: keyword={}, type={}, agency={}, year={}, page={}, size={}", keyword, type, agency, year, page, size);
        try {
            Page<LegalDocumentDTO> result = service.searchDocuments(keyword, type, agency, year, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Exception during searchDocuments: ", e);
            throw e;
        }
    }

}