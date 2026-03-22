package com.example.demo.controller;

import com.example.demo.model.dto.SupportRequestDTO;
import com.example.demo.service.SupportRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-request")
@RequiredArgsConstructor
@Slf4j
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    @PostMapping
    public ResponseEntity<SupportRequestDTO> createSupportRequest(@Valid @RequestBody SupportRequestDTO dto) {
        log.info("Received request to create Support Request: {}", dto.getSubject());
        try {
            return ResponseEntity.ok(supportRequestService.create(dto));
        } catch (Exception e) {
            log.error("Exception during createSupportRequest: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportRequestDTO> updateSupportRequest(@PathVariable Integer id, @Valid @RequestBody SupportRequestDTO dto) {
        log.info("Received request to update Support Request with id: {}", id);
        dto.setId(id);
        try {
            return ResponseEntity.ok(supportRequestService.update(dto));
        } catch (Exception e) {
            log.error("Exception during updateSupportRequest for id {}: ", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportRequest(@PathVariable Integer id) {
        log.info("Received request to delete Support Request with id: {}", id);
        try {
            supportRequestService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Exception during deleteSupportRequest for id {}: ", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportRequestDTO> getSupportRequestById(@PathVariable Integer id) {
        log.info("Received request to fetch Support Request with id: {}", id);
        try {
            return ResponseEntity.ok(supportRequestService.findById(id));
        } catch (Exception e) {
            log.error("Exception during getSupportRequestById for id {}: ", id, e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<SupportRequestDTO>> getAllSupportRequests() {
        log.info("Received request to fetch all Support Requests");
        try {
            return ResponseEntity.ok(supportRequestService.findAll());
        } catch (Exception e) {
            log.error("Exception during getAllSupportRequests: ", e);
            throw e;
        }
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<?> replySupportRequest(
            @PathVariable Integer id, 
            @Valid @RequestBody com.example.demo.model.dto.SupportRequestReplyDTO replyDTO) {
        log.info("Received request to reply to Support Request id: {}", id);
        try {
            supportRequestService.replyToSupportRequest(id, replyDTO);
            return ResponseEntity.ok(java.util.Map.of("message", "Sent reply email successfully!"));
        } catch (Exception e) {
            log.error("Error replying to Support Request {}: ", id, e);
            throw e;
        }
    }
}
