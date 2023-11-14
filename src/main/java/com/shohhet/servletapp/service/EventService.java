package com.shohhet.servletapp.service;

import com.shohhet.servletapp.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.dto.GetEventRequestDto;
import com.shohhet.servletapp.mapper.EventToDtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EventService {
    private final EventRepositoryImpl eventRepository;
    private final EventToDtoMapper eventToDtoMapper;

    @Transactional
    public Optional<GetEventRequestDto> getById(Integer id) {
        return eventRepository.getById(id).map(eventToDtoMapper::mapFrom);
    }

    @Transactional
    public List<GetEventRequestDto> getAll() {
        return eventRepository.getAll().stream()
                .map(eventToDtoMapper::mapFrom)
                .toList();
    }

}
