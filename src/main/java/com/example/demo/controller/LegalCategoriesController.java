package com.example.demo.controller;

import com.example.demo.model.dto.LegalCategoriesDTO;
import com.example.demo.service.LegalCategoriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legal-categories")
@RequiredArgsConstructor
@Slf4j
public class LegalCategoriesController {

    private final LegalCategoriesService service;

    @PostMapping
    public ResponseEntity<LegalCategoriesDTO> createCategory(@Valid @RequestBody LegalCategoriesDTO dto) {
        log.info("Received request to create legal category: {}", dto.getName());
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (Exception e) {
            log.error("Exception during createCategory: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalCategoriesDTO> updateCategory(@PathVariable Integer id, @Valid @RequestBody LegalCategoriesDTO dto) {
        log.info("Received request to update legal category id: {}", id);
        dto.setId(id);
        try {
            return ResponseEntity.ok(service.update(dto));
        } catch (Exception e) {
            log.error("Exception during updateCategory: ", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        log.info("Received request to delete legal category id: {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Exception during deleteCategory: ", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalCategoriesDTO> getCategoryById(@PathVariable Integer id) {
        log.info("Received request to fetch legal category id: {}", id);
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (Exception e) {
            log.error("Exception during getCategoryById: ", e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<LegalCategoriesDTO>> getAllCategories() {
        log.info("Received request to fetch all legal categories");
        try {
            return ResponseEntity.ok(service.findAll());
        } catch (Exception e) {
            log.error("Exception during getAllCategories: ", e);
            throw e;
        }
    }
}
