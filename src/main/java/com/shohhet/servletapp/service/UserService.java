package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.model.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.service.dto.UpdateUserDto;
import com.shohhet.servletapp.service.dto.userDto.UserNameDto;
import com.shohhet.servletapp.service.dto.userDto.UserDto;
import com.shohhet.servletapp.service.mapper.DtoToUserMapper;
import com.shohhet.servletapp.service.mapper.UserToDtoMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserService {
    private final UserRepositoryImpl userRepository;
    private final UserToDtoMapper userToDtoMapper;
    private final DtoToUserMapper dtoToUserMapper;

    @Transactional
    public Optional<UserDto> getById(Integer id) {
        return userRepository
                .getById(id)
                .stream()
                .map(userToDtoMapper::mapFrom)
                .findFirst();
    }

    @Transactional
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(userToDtoMapper::mapFrom)
                .toList();
    }

    @Transactional
    public Optional<UserDto> add(UserNameDto userDto) {
        var maybeUser = userRepository.getAll().stream()
                .filter(entity -> entity.getName().equals(userDto.name()))
                .findFirst();
        if (maybeUser.isEmpty()) {
            var user = userRepository.add(dtoToUserMapper.mapFrom(userDto));
            return Optional.of(userToDtoMapper.mapFrom(user));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<UserDto> update(UserDto userDto) {
        var maybeUser = userRepository.getById(userDto.id());
        if (maybeUser.isPresent()) {
            var user = maybeUser.get();
            user.setName(userDto.name());
            userRepository.update(user);
            return Optional.of(userToDtoMapper.mapFrom(user));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean delete(Integer id) {
        var maybeUser = userRepository.getById(id);
        if (maybeUser.isPresent()) {
            userRepository.delete(maybeUser.get());
            return true;
        }
        return false;
    }

}
