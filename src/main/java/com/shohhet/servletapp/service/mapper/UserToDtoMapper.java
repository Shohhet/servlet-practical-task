package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.service.dto.userDto.UserDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserToDtoMapper implements Mapper<UserEntity, UserDto> {
    private final EventToDtoMapper eventToDtoMapper;

    @Override
    public UserDto mapFrom(UserEntity entity) {
        return new UserDto(entity.getId(),
                entity.getName(),
                entity.getEvents()
                        .stream()
                        .map(eventToDtoMapper::mapFrom)
                        .toList());
    }
}
