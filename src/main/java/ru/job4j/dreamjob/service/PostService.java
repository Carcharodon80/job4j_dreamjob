package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.PostDBStore;

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
        Post post = store.findById(id);
        post.setCity(cityService.findById(post.getCity().getId()));
        return post;
    }

    public void update(Post post) {
        store.update(post);
    }

}
