package com.example.demo.convert;

import com.example.demo.entity.LegalCategoriesEntity;
import com.example.demo.entity.LegalDocumentEntity;
import com.example.demo.model.dto.LegalDocumentDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegalDocumentConvert {
    private final ModelMapper modelMapper;

    public LegalDocumentEntity toEntity(LegalDocumentDTO dto) {
        LegalDocumentEntity entity = modelMapper.map(dto, LegalDocumentEntity.class);
        if (dto.getCategoryId() != null) {
            LegalCategoriesEntity category = new LegalCategoriesEntity();
            category.setId(dto.getCategoryId());
            entity.setCategory(category);
        }
        return entity;
    }

    public LegalDocumentDTO toDTO(LegalDocumentEntity entity) {
        LegalDocumentDTO dto = modelMapper.map(entity, LegalDocumentDTO.class);
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
        }
        return dto;
    }
}
