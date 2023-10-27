package com.shohhet.servletapp.service;

import com.shohhet.servletapp.model.repository.impl.FileRepositoryImpl;
import com.shohhet.servletapp.service.dto.fileDto.FileDto;
import com.shohhet.servletapp.service.mapper.DtoToFileMapper;
import com.shohhet.servletapp.service.mapper.FileToDtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FileService {
    private final FileRepositoryImpl fileRepository;
    private final FileToDtoMapper fileToDtoMapper;
    private final DtoToFileMapper dtoToFileMapper;

    @Transactional
    public Optional<FileDto> add(FileDto fileDto) {
        var maybeFile = fileRepository.getAll().stream()
                .filter(fileEntity ->
                        fileEntity.getName().equals(fileDto.name()) &&
                        fileEntity.getPath().equals(fileDto.path()))
                .findFirst();
        if (maybeFile.isEmpty()) {
            var file = fileRepository.add(dtoToFileMapper.mapFrom(fileDto));
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
}
