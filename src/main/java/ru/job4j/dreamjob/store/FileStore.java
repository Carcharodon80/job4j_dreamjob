package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.File;

import java.util.Optional;

public interface FileStore {
    File save(File file);

    Optional<File> findById(int id);

    boolean deleteById(int id);
}
