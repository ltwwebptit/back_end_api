package com.example.demo.convert;

import org.modelmapper.ModelMapper;
import com.example.demo.entity.UsersEntity;
import com.example.demo.model.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConvert {
    private final ModelMapper modelMapper;

    public UsersEntity toUserEntity(RegisterDTO registerDTO) {
        UsersEntity userEntity = modelMapper.map(registerDTO, UsersEntity.class);
        userEntity.setRolename("CUSTOMER");
        userEntity.setStatus(0);
        return userEntity;
    }
}
