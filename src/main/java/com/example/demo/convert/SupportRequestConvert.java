package com.example.demo.convert;

import com.example.demo.entity.SupportRequestsEntity;
import com.example.demo.entity.UsersEntity;
import com.example.demo.model.dto.SupportRequestDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportRequestConvert {
    private final ModelMapper modelMapper;

    public SupportRequestsEntity toEntity(SupportRequestDTO dto) {
        SupportRequestsEntity entity = modelMapper.map(dto, SupportRequestsEntity.class);
        if (dto.getUserId() != null) {
            UsersEntity user = new UsersEntity();
            user.setId(dto.getUserId());
            entity.setUser(user);
        }
        return entity;
    }

    public SupportRequestDTO toDTO(SupportRequestsEntity entity) {
        SupportRequestDTO dto = modelMapper.map(entity, SupportRequestDTO.class);
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        return dto;
    }
}
