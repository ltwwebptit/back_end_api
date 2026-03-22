package com.example.demo.controller;

import com.example.demo.model.dto.FAQDTO;
import com.example.demo.service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class FAQController {

    private final FAQService faqService;

    @PostMapping
    public ResponseEntity<FAQDTO> createFAQ(@Valid @RequestBody FAQDTO faqDTO) {
        log.info("Received request to create FAQ: {}", faqDTO.getQuestion());
        try {
            return ResponseEntity.ok(faqService.create(faqDTO));
        } catch (Exception e) {
            log.error("Exception during createFAQ: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FAQDTO> updateFAQ(@PathVariable Integer id, @Valid @RequestBody FAQDTO faqDTO) {
        log.info("Received request to update FAQ with id: {}", id);
        faqDTO.setId(id);
        try {
            return ResponseEntity.ok(faqService.update(faqDTO));
        } catch (Exception e) {
            log.error("Exception during updateFAQ for id {}: ", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Integer id) {
        log.info("Received request to delete FAQ with id: {}", id);
        try {
            faqService.deleteFAQ(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Exception during deleteFAQ for id {}: ", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FAQDTO> getFAQById(@PathVariable Integer id) {
        log.info("Received request to fetch FAQ with id: {}", id);
        try {
            return ResponseEntity.ok(faqService.findById(id));
        } catch (Exception e) {
            log.error("Exception during getFAQById for id {}: ", id, e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<FAQDTO>> getAllFAQs() {
        log.info("Received request to fetch all FAQs");
        try {
            return ResponseEntity.ok(faqService.findAll());
        } catch (Exception e) {
            log.error("Exception during getAllFAQs: ", e);
            throw e;
        }
    }
}
