package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.service.dto.userDto.UserDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class UserToDtoMapper implements Mapper<UserEntity, UserDto> {
    private final EventToDtoMapper eventToDtoMapper;

    @Override
    public UserDto mapFrom(UserEntity entity) {
        return new UserDto(entity.getId(),
                entity.getName(),
                Objects.nonNull(entity.getEvents()) ?
                        entity.getEvents()
                                .stream()
                                .map(eventToDtoMapper::mapFrom)
                                .toList() :
                        List.of());
    }
}
