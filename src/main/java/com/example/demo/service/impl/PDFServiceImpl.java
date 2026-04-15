package com.example.demo.service.impl;

import com.example.demo.model.dto.LegalDocumentDTO;
import com.example.demo.service.PDFService;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.lowagie.text.Document;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PDFServiceImpl implements PDFService {
    private final RestTemplate restTemplate;
    @Override
    public void sendPdfToAnotherService(byte[] pdfBytes, String fileName) {
        String url = "https://webhook.site/b06954e2-6aa7-4cd8-b54f-1deb76f6cd3a";
        ByteArrayResource byteArrayResource = new ByteArrayResource(pdfBytes){
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("file", byteArrayResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("gui thanh cong"+response.getBody());;
            }
            else {
                System.out.println("gui that bai"+response.getStatusCode());
            }

        }
        catch (Exception e) {
            System.out.println("loi that bai khi goi"+e.getMessage());
        }

    }

    @Override
    public byte[] genaratePdf(LegalDocumentDTO dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try{
            PdfWriter.getInstance(document, baos);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA);
            document.add(new Paragraph("link "+dto.getLink(),font));

            document.close();
        }
        catch (Exception e) {
            System.out.println("loi that bai"+e.getMessage());
        }
        return baos.toByteArray();
    }
}
