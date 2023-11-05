package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.entity.EventEntity;
import com.shohhet.servletapp.model.entity.UserEntity;
import com.shohhet.servletapp.model.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.model.repository.impl.FileRepositoryImpl;
import com.shohhet.servletapp.model.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.service.dto.fileDto.FileDto;
import com.shohhet.servletapp.service.dto.fileDto.UploadFileDto;
import com.shohhet.servletapp.service.mapper.UploadDtoToFileMapper;
import com.shohhet.servletapp.service.mapper.FileToDtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FileService {
    private final FileRepositoryImpl fileRepository;
    private final UserRepositoryImpl userRepository;
    private final EventRepositoryImpl eventRepository;
    private final FileToDtoMapper fileToDtoMapper;
    private final UploadDtoToFileMapper dtoToFileMapper;

    @Transactional
    public Optional<FileDto> add(UploadFileDto uploadFileDto) throws IOException {
        var maybeUser = userRepository.getById(uploadFileDto.userId());
        if (maybeUser.isPresent()) {
            var maybeFile = fileRepository.getAll().stream()
                    .filter(fileEntity ->
                            fileEntity.getName().equals(uploadFileDto.name()) &&
                            fileEntity.getPath().equals(uploadFileDto.path()))
                    .findFirst();
            if (maybeFile.isEmpty()) {
                var file = fileRepository.add(dtoToFileMapper.mapFrom(uploadFileDto));
                upload(uploadFileDto);
                var event = EventEntity.builder()
                        .user(maybeUser.get())
                        .file(file)
                        .build();
                eventRepository.add(event);
                return Optional.of(fileToDtoMapper.mapFrom(file));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<FileDto> getById(Integer id) {
        return fileRepository.getById(id)
                .map(fileToDtoMapper::mapFrom);
    }

    @Transactional
    public List<FileDto> getAll() {
        return fileRepository.getAll().stream()
                .map(fileToDtoMapper::mapFrom)
                .toList();
    }

    private void upload(UploadFileDto fileDto) throws IOException {
        var fullPath = Path.of(fileDto.path() + File.separator + fileDto.name());
        try(var is = fileDto.fileInputStream()) {
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, is.readAllBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
