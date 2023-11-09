package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.service.dto.userDto.UserDto;


public class UserToDtoMapper implements Mapper<UserEntity, UserDto> {

    @Override
    public UserDto mapFrom(UserEntity entity) {
        return new UserDto(entity.getId(),
                entity.getName());
    }
}
