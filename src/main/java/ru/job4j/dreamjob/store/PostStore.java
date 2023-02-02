package ru.job4j.dreamjob.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Post;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class PostStore {
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger(0);

    private PostStore() {
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    /**
     * устанавливает id для post и добавляет post в posts
     */
    public void add(Post post) {
        post.setId(id.incrementAndGet());
        posts.put(post.getId(), post);
    }

    public Optional<Post> findById(int id) {
        return Optional.of(posts.get(id));
    }

    public void update(Post post) {
        posts.replace(post.getId(), post);
    }
}
