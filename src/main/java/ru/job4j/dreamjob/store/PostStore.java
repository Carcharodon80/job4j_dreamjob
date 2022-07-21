package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Post;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PostStore {
    private static final PostStore INST = new PostStore();

    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    private PostStore() {
        posts.put(1, new Post(1, "Junior Java Job", "Junior",
                new GregorianCalendar(2022, Calendar.JANUARY, 15, 22, 15, 36)));
        posts.put(2, new Post(2, "Middle Java Job", "Middle",
                new GregorianCalendar(2021, Calendar.JUNE, 25, 16, 00, 00)));
        posts.put(3, new Post(3, "Senior Java Job", "Senior",
                Calendar.getInstance()));
    }

    public static PostStore instOf() {
        return INST;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }
}
