package com.shohhet.servletapp.mapper;

import com.shohhet.servletapp.entity.EventEntity;
import com.shohhet.servletapp.dto.GetEventRequestDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventToDtoMapper implements Mapper<EventEntity, GetEventRequestDto> {
    private final UserToDtoMapper userToDtoMapper;
    private final FileToDtoMapper fileToDtoMapper;

    @Override
    public GetEventRequestDto mapFrom(EventEntity entity) {
        return new GetEventRequestDto(
                entity.getId(),
                userToDtoMapper.mapFrom(entity.getUser()),
                fileToDtoMapper.mapFrom(entity.getFile())
        );
    }
}
