package ru.job4j.dreamjob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.store.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleFileService implements FileService {
    private final FileStore fileStore;
    private final String storageDirectory;

    /**
     * @param storageDirectory - получаем из application.properties
     */
    public SimpleFileService(FileStore fileStore,
                             @Value("${file.directory}") String storageDirectory) {
        this.fileStore = fileStore;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает FileDto, создает из него File и сохраняет в storageDirectory
     */
    @Override
    public File save(FileDto fileDto) {
        String path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        return fileStore.save(new File(fileDto.getName(), path));
    }

    /**
     * Создает уникальный путь для сохраняемого файла
     */
    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Находит файл в fileStore, затем читает его содержимое из файловой системы, создает FileDto
     */
    @Override
    public Optional<FileDto> getFileById(int id) {
        Optional<File> fileOptional = fileStore.findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }
        byte[] content = readFileAsBytes(fileOptional.get().getPath());
        return Optional.of(new FileDto(fileOptional.get().getName(), content));
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * удаляет файл из fileStore и из файловой системы
     */
    @Override
    public boolean deleteById(int id) {
        Optional<File> fileOptional = fileStore.findById(id);
        if (fileOptional.isEmpty()) {
            return false;
        }
        deleteFile(fileOptional.get().getPath());
        return fileStore.deleteById(id);
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
