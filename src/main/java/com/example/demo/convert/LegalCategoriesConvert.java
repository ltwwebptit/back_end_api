package com.example.demo.convert;

import com.example.demo.entity.LegalCategoriesEntity;
import com.example.demo.model.dto.LegalCategoriesDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LegalCategoriesConvert {
    private final ModelMapper modelMapper;

    public LegalCategoriesEntity toEntity(LegalCategoriesDTO dto) {
        return modelMapper.map(dto, LegalCategoriesEntity.class);
    }

    public LegalCategoriesDTO toDTO(LegalCategoriesEntity entity) {
        return modelMapper.map(entity, LegalCategoriesDTO.class);
    }
}
