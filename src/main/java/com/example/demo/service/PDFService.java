package com.example.demo.service;

import com.example.demo.model.dto.LegalDocumentDTO;
import org.springframework.web.client.RestTemplate;

public interface PDFService {
    void sendPdfToAnotherService(byte[] pdfBytes, String fileName);
    byte[] genaratePdf(LegalDocumentDTO dto);
}
