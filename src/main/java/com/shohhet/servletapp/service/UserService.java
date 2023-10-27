package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.service.dto.userDto.UserDto;
import com.shohhet.servletapp.service.mapper.UserToDtoMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class UserService {
    private final UserRepositoryImpl userRepository;
    private final UserToDtoMapper userToDtoMapper;
    @Transactional
    public Optional<UserDto> get(Integer id) {
        return userRepository
                .getById(id)
                .stream()
                .map(userToDtoMapper::mapFrom)
                .findFirst();
    }

}
