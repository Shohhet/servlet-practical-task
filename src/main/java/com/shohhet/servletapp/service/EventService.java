package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.service.dto.eventDto.EventDto;
import com.shohhet.servletapp.service.mapper.EventToDtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EventService {
    private final EventRepositoryImpl eventRepository;
    private final EventToDtoMapper eventToDtoMapper;

    @Transactional
    public Optional<EventDto> getById(Integer id) {
        return eventRepository.getById(id).map(eventToDtoMapper::mapFrom);
    }

    @Transactional
    public List<EventDto> getAll() {
        return eventRepository.getAll().stream()
                .map(eventToDtoMapper::mapFrom)
                .toList();
    }

}
