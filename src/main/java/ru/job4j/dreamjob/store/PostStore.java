package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PostStore - синглтон (приватный контструктор)
 */
public class PostStore {
    private static final PostStore INST = new PostStore();

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        posts.put(1, new Post(1, "Junior Java Job", "Junior",
                LocalDateTime.of(2022, Month.JANUARY, 15, 22, 15, 36)));
        posts.put(2, new Post(2, "Middle Java Job", "Middle",
                LocalDateTime.of(2021, Month.JUNE, 25, 16, 0, 0)));
        posts.put(3, new Post(3, "Senior Java Job", "Senior",
                LocalDateTime.now()));
    }

    public static PostStore instOf() {
        return INST;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    /**
     * ищет свободный id, устанавливает его для post и добавляет post в posts
     */
    public void add(Post post) {
        int id = post.getId();
        while (posts.containsKey(id)) {
            id++;
        }
        post.setId(id);
        posts.put(id, post);
    }

    public Post findById(int id) {
        return posts.get(id);
    }

    public void update(Post post) {
        posts.replace(post.getId(), post);
    }
}
