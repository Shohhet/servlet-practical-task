package com.shohhet.servletapp.service;

import com.shohhet.servletapp.entity.EventEntity;
import com.shohhet.servletapp.repository.impl.EventRepositoryImpl;
import com.shohhet.servletapp.repository.impl.FileRepositoryImpl;
import com.shohhet.servletapp.repository.impl.UserRepositoryImpl;
import com.shohhet.servletapp.dto.GetFileRequestDto;
import com.shohhet.servletapp.dto.UploadFileRequestDto;
import com.shohhet.servletapp.mapper.UploadDtoToFileMapper;
import com.shohhet.servletapp.mapper.FileToDtoMapper;
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
    public Optional<GetFileRequestDto> add(UploadFileRequestDto uploadFileDto) throws IOException {
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
    public Optional<GetFileRequestDto> getById(Integer id) {
        return fileRepository.getById(id)
                .map(fileToDtoMapper::mapFrom);
    }

    @Transactional
    public List<GetFileRequestDto> getAll() {
        return fileRepository.getAll().stream()
                .map(fileToDtoMapper::mapFrom)
                .toList();
    }

    private void upload(UploadFileRequestDto fileDto) throws IOException {
        var fullPath = Path.of(fileDto.path() + File.separator + fileDto.name());
        try(var is = fileDto.fileInputStream()) {
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, is.readAllBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
