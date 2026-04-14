package com.example.demo.service.impl;

import com.example.demo.convert.LegalDocumentConvert;
import com.example.demo.entity.LegalCategoriesEntity;
import com.example.demo.entity.LegalDocumentEntity;
import com.example.demo.model.dto.LegalDocumentDTO;
import com.example.demo.repository.LegalDocumentRepository;
import com.example.demo.service.LegalDocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
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
            entity.setStatus(true);
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

        if (dto.getLink() != null) {
            existingEntity.setLink(dto.getLink());
        }

        if (dto.getStatus() != null) {
            existingEntity.setStatus(dto.getStatus());
        }

        if (dto.getCategoryId() != null) {
            LegalCategoriesEntity category = new LegalCategoriesEntity();
            category.setId(dto.getCategoryId());
            existingEntity.setCategory(category);
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
    @Transactional
    public LegalDocumentDTO findById(Integer id) {
        log.info("Fetching Legal Document with id: {}", id);
        repository.incrementViewCount(id);
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

    @Override
    public List<LegalDocumentDTO> findMaxCount(List<LegalDocumentDTO> dtos) {
        dtos = repository.findAll().stream().map(convert::toDTO).collect(Collectors.toList());
        if (dtos == null || dtos.size() == 0) {
            return Collections.emptyList();
        }
        int maxViewCount = dtos.stream()
                .mapToInt(dto -> dto.getCount() == null ? 0 : dto.getCount())
                .max()
                .orElse(0);
        return dtos.stream().filter(dto -> {
            int count = dto.getCount() == null ? 0 : dto.getCount();
            return count == maxViewCount;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<LegalDocumentDTO> searchDocuments(String keyword, String type, String agency, Integer year, int page, int size) {
        log.info("Searching Legal Documents: keyword={}, type={}, agency={}, year={}, page={}, size={}", keyword, type, agency, year, page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "issueDate"));
        String kw = (keyword != null && !keyword.isBlank()) ? keyword : null;
        String tp = (type != null && !type.isBlank()) ? type : null;
        String ag = (agency != null && !agency.isBlank()) ? agency : null;
        return repository.searchDocuments(kw, tp, ag, year, pageable);
    }
}
