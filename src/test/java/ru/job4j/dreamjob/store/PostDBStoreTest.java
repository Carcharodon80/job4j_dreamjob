package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostDBStoreTest {
    private static PostDBStore store;
    private static BasicDataSource pool;
    private static final String DELETE_ALL = "DELETE FROM post";

    @BeforeAll
    public static void createStore() {
        pool = new Main().loadPool();
        store = new PostDBStore(pool);
    }

    /**
     * Если не очистить store, то посты в БД накапливаются, и findAll() не проходит
     */
    @BeforeEach
    public void clearStore() {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_ALL)
        ) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenAddPost() {
        Post post = new Post(0, "Java Job", "Description", LocalDateTime.now(), true,
                new City(0, "London"), 0);
        store.add(post);
        Post postInDb = store.findById(post.getId());
        assertEquals(postInDb.getName(), post.getName());
    }

    @Test
    public void whenFindById() {
        Post post = new Post(0, "Java Job", "Description", LocalDateTime.now(), true,
                new City(0, "London"), 0);
        store.add(post);
        Post postInDb = store.findById(post.getId());
        assertEquals(postInDb, post);
    }

    @Test
    public void whenUpdate() {
        Post post = new Post(0, "Java Job", "Description", LocalDateTime.now(), true,
                new City(0, "London"), 0);
        store.add(post);
        post.setName("New Java Job");
        store.update(post);
        Post postInDb = store.findById(post.getId());
        assertEquals(postInDb.getName(), post.getName());
    }

    @Test
    public void whenFindAll() {
        Post post = new Post(0, "Java Job", "Description", LocalDateTime.now(), true,
                new City(0, "London"), 0);
        Post post2 = new Post(4, "Java Job4", "Description", LocalDateTime.now(), true,
                new City(0, "London"), 0);
        List<Post> posts = Arrays.asList(post, post2);
        store.add(post);
        store.add(post2);
        assertEquals(store.findAll(), posts);
    }
}