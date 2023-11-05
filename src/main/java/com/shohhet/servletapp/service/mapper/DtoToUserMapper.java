package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.service.dto.userDto.UserNameDto;


public class DtoToUserMapper implements Mapper<UserNameDto, UserEntity>{
    @Override
    public UserEntity mapFrom(UserNameDto dto) {
        return UserEntity.builder()
                .name(dto.name())
                .build();
    }
}
