package com.example.demo.service.impl;

import com.example.demo.convert.LegalDocumentConvert;
import com.example.demo.entity.LegalCategoriesEntity;
import com.example.demo.entity.LegalDocumentEntity;
import com.example.demo.model.dto.LegalDocumentDTO;
import com.example.demo.repository.LegalDocumentRepository;
import com.example.demo.service.LegalDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LegalDocumentServiceImpl implements LegalDocumentService {

    private final LegalDocumentRepository repository;
    private final LegalDocumentConvert convert;

    @Override
    public LegalDocumentDTO create(LegalDocumentDTO dto) {
        log.info("Creating new Legal Document: {}", dto.getTitle());
        LegalDocumentEntity entity = convert.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        if (entity.getStatus() == null) {
            entity.setStatus(true); // default active
        }

        entity = repository.save(entity);
        log.info("Successfully created Legal Document with id: {}", entity.getId());
        return convert.toDTO(entity);
    }

    @Override
    public LegalDocumentDTO update(LegalDocumentDTO dto) {
        log.info("Updating Legal Document with id: {}", dto.getId());
        LegalDocumentEntity existingEntity = repository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Legal Document update failed: not found with id: {}", dto.getId());
                    return new RuntimeException("Legal Document not found");
                });

        existingEntity.setTitle(dto.getTitle());
        existingEntity.setContent(dto.getContent());
        existingEntity.setType(dto.getType());
        existingEntity.setUpdatedAt(LocalDateTime.now());
        existingEntity.setIssuingAgency(dto.getIssuingAgency());
        existingEntity.setIssueDate(dto.getIssueDate());
        
        if (dto.getStatus() != null) {
            existingEntity.setStatus(dto.getStatus());
        }

        if (dto.getCategoryId() != null) {
            LegalCategoriesEntity category = new LegalCategoriesEntity();
            category.setId(dto.getCategoryId());
            existingEntity.setCategory(category);
            existingEntity.setCategoryId(dto.getCategoryId());
        }

        existingEntity = repository.save(existingEntity);
        log.info("Successfully updated Legal Document with id: {}", existingEntity.getId());
        return convert.toDTO(existingEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting Legal Document with id: {}", id);
        try {
            repository.deleteById(id);
            log.info("Successfully deleted Legal Document with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting Legal Document with id: {}", id, e);
            throw new RuntimeException("Error deleting Legal Document", e);
        }
    }

    @Override
    public LegalDocumentDTO findById(Integer id) {
        log.info("Fetching Legal Document with id: {}", id);
        return repository.findById(id)
                .map(convert::toDTO)
                .orElseThrow(() -> {
                    log.error("Legal Document fetch failed: not found with id: {}", id);
                    return new RuntimeException("Legal Document not found");
                });
    }

    @Override
    public List<LegalDocumentDTO> findAll() {
        log.info("Fetching all Legal Documents");
        return repository.findAll().stream()
                .map(convert::toDTO)
                .collect(Collectors.toList());
    }
}
