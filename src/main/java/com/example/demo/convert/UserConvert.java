package com.example.demo.convert;

import com.example.demo.model.dto.UserDTO;
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
        userEntity.setStatus(1);
        return userEntity;
    }
    public UserDTO toUserDTO(UsersEntity userEntity) {
        UserDTO userdto = modelMapper.map(userEntity, UserDTO.class);
        userdto.setUserId(userEntity.getId());
        return userdto;
    }
}
