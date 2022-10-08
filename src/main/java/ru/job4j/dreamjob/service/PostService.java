package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostDBStore;
import ru.job4j.dreamjob.store.PostStore;

import java.util.Collection;
import java.util.List;

@ThreadSafe
@Service
public class PostService {
    private final PostDBStore store;
    private final CityService cityService;

    public PostService(PostDBStore store) {
        this.store = store;
        cityService = new CityService();
    }

    public Collection<Post> findAll() {
        List<Post> posts = store.findAll();
        posts.forEach(post -> post.setCity(
                cityService.findById(post.getCity().getId())
        ));
        return posts;
    }

    public void add(Post post) {
        store.add(post);
    }

    public Post findById(int id) {
        return store.findById(id);
    }

    public void update(Post post) {
        store.update(post);
    }

}
