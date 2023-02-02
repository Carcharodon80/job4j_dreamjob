package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostDBStore;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ThreadSafe
@Service
public class PostService {
    private final PostDBStore postStore;
    private final CityService cityService;
    private final FileService fileService;

    public PostService(PostDBStore postStore, FileService fileService) {
        this.postStore = postStore;
        this.cityService = new CityService();
        this.fileService = fileService;
    }

    public Collection<Post> findAll() {
        List<Post> posts = postStore.findAll();
        posts.forEach(post -> post.setCity(
                cityService.findById(post.getCity().getId())
        ));
        return posts;
    }

    public Post add(Post post, FileDto image) {
        saveNewFile(post, image);
        return postStore.add(post);
    }

    private void saveNewFile(Post post, FileDto image) {
        File file = fileService.save(image);
        post.setFileId(file.getId());
    }

    public Optional<Post> findById(int id) {
        Post post = postStore.findById(id);
        post.setCity(cityService.findById(post.getCity().getId()));
        return Optional.of(post);
    }

    /**
     * при удалении вакансии из файловой системы удаляется связанный с ней файл
     */
    public boolean delete(int id) {
        Optional<Post> optionalPost = findById(id);
        if (optionalPost.isEmpty()) {
            return false;
        }
        boolean isDeleted = postStore.deletePost(id);
        fileService.deleteById(optionalPost.get().getFileId());
        return isDeleted;
    }

    /**
     * Если передан не пустой файл, то старый файл удаляем, а новый сохраняем
     */
    public boolean update(Post post, FileDto image) {
        boolean isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return postStore.update(post);
        }
        int oldFileId = post.getFileId();
        saveNewFile(post, image);
        boolean isUpdated = postStore.update(post);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

}
