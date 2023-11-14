package com.shohhet.servletapp.service;

import com.shohhet.servletapp.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.dto.CreateUserRequestDto;
import com.shohhet.servletapp.dto.GetAndUpdateUserRequestDto;
import com.shohhet.servletapp.mapper.DtoToUserMapper;
import com.shohhet.servletapp.mapper.UserToDtoMapper;
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
    public Optional<GetAndUpdateUserRequestDto> getById(Integer id) {
        return userRepository
                .getById(id)
                .stream()
                .map(userToDtoMapper::mapFrom)
                .findFirst();
    }

    @Transactional
    public List<GetAndUpdateUserRequestDto> getAll() {
        return userRepository.getAll().stream()
                .map(userToDtoMapper::mapFrom)
                .toList();
    }

    @Transactional
    public Optional<GetAndUpdateUserRequestDto> add(CreateUserRequestDto userDto) {
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
    public Optional<GetAndUpdateUserRequestDto> update(GetAndUpdateUserRequestDto getAndUpdateUserRequestDto) {
        var maybeUser = userRepository.getById(getAndUpdateUserRequestDto.id());
        if (maybeUser.isPresent()) {
            var user = maybeUser.get();
            user.setName(getAndUpdateUserRequestDto.name());
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
