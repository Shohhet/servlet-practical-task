package com.shohhet.servletapp.mapper;

import com.shohhet.servletapp.entity.UserEntity;
import com.shohhet.servletapp.dto.CreateUserRequestDto;


public class DtoToUserMapper implements Mapper<CreateUserRequestDto, UserEntity>{
    @Override
    public UserEntity mapFrom(CreateUserRequestDto dto) {
        return UserEntity.builder()
                .name(dto.name())
                .build();
    }
}
