package com.example.demo.service.impl;

import com.example.demo.convert.LegalCategoriesConvert;
import com.example.demo.entity.LegalCategoriesEntity;
import com.example.demo.model.dto.LegalCategoriesDTO;
import com.example.demo.repository.LegalCategoriesRepository;
import com.example.demo.service.LegalCategoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LegalCategoriesServiceImpl implements LegalCategoriesService {

    private final LegalCategoriesRepository repository;
    private final LegalCategoriesConvert convert;

    @Override
    public LegalCategoriesDTO create(LegalCategoriesDTO dto) {
        log.info("Creating new Legal Category: {}", dto.getName());
        LegalCategoriesEntity entity = convert.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity = repository.save(entity);
        log.info("Successfully created Legal Category with id: {}", entity.getId());
        return convert.toDTO(entity);
    }

    @Override
    public LegalCategoriesDTO update(LegalCategoriesDTO dto) {
        log.info("Updating Legal Category with id: {}", dto.getId());
        LegalCategoriesEntity existingEntity = repository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Legal Category update failed: not found with id: {}", dto.getId());
                    return new RuntimeException("Legal Category not found");
                });

        existingEntity.setName(dto.getName());
        existingEntity.setUpdatedAt(LocalDateTime.now());

        existingEntity = repository.save(existingEntity);
        log.info("Successfully updated Legal Category with id: {}", existingEntity.getId());
        return convert.toDTO(existingEntity);
    }

    @Override
    public void delete(Integer id) {
        log.info("Deleting Legal Category with id: {}", id);
        try {
            repository.deleteById(id);
            log.info("Successfully deleted Legal Category with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting Legal Category with id: {}", id, e);
            throw new RuntimeException("Error deleting Legal Category", e);
        }
    }

    @Override
    public LegalCategoriesDTO findById(Integer id) {
        log.info("Fetching Legal Category with id: {}", id);
        return repository.findById(id)
                .map(convert::toDTO)
                .orElseThrow(() -> {
                    log.error("Legal Category fetch failed: not found with id: {}", id);
                    return new RuntimeException("Legal Category not found");
                });
    }

    @Override
    public List<LegalCategoriesDTO> findAll() {
        log.info("Fetching all Legal Categories");
        List<LegalCategoriesEntity> list = repository.findAll();
        return list.stream()
                .map(convert::toDTO)
                .collect(Collectors.toList());
    }
}
