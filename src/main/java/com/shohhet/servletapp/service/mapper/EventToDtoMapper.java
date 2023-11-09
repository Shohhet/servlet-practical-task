package com.shohhet.servletapp.service.mapper;

import com.shohhet.servletapp.model.entity.EventEntity;
import com.shohhet.servletapp.service.dto.eventDto.EventDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventToDtoMapper implements Mapper<EventEntity, EventDto> {
    private final UserToDtoMapper userToDtoMapper;
    private final FileToDtoMapper fileToDtoMapper;

    @Override
    public EventDto mapFrom(EventEntity entity) {
        return new EventDto(
                entity.getId(),
                userToDtoMapper.mapFrom(entity.getUser()),
                fileToDtoMapper.mapFrom(entity.getFile())
        );
    }
}
