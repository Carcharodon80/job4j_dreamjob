package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PostStore - синглтон (приватный конструктор)
 */
public class PostStore {
    private static final PostStore INST = new PostStore();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger(0);

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
     * проверяет id (если такой есть в posts - увеличивает на 1), устанавливает его для post и добавляет post в posts
     */
    public void add(Post post) {
        while (posts.containsKey(id.intValue())) {
            id.incrementAndGet();
        }
        post.setId(id.intValue());
        posts.put(post.getId(), post);
    }

    public Post findById(int id) {
        return posts.get(id);
    }

    public void update(Post post) {
        posts.replace(post.getId(), post);
    }
}
