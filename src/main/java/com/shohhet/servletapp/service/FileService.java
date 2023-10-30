package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.repository.impl.FileRepositoryImpl;
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
    private final FileToDtoMapper fileToDtoMapper;
    private final UploadDtoToFileMapper dtoToFileMapper;

    @Transactional
    public Optional<FileDto> add(UploadFileDto fileDto) throws IOException {
        var maybeFile = fileRepository.getAll().stream()
                .filter(fileEntity ->
                        fileEntity.getName().equals(fileDto.name()) &&
                        fileEntity.getPath().equals(fileDto.path()))
                .findFirst();
        if (maybeFile.isEmpty()) {
            var file = fileRepository.add(dtoToFileMapper.mapFrom(fileDto));
            upload(fileDto);
            return Optional.of(fileToDtoMapper.mapFrom(file));
        }
        return Optional.empty();
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
