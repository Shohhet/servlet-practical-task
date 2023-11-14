package com.shohhet.servletapp.mapper;

import com.shohhet.servletapp.entity.UserEntity;
import com.shohhet.servletapp.dto.GetAndUpdateUserRequestDto;


public class UserToDtoMapper implements Mapper<UserEntity, GetAndUpdateUserRequestDto> {

    @Override
    public GetAndUpdateUserRequestDto mapFrom(UserEntity entity) {
        return new GetAndUpdateUserRequestDto(entity.getId(),
                entity.getName());
    }
}
