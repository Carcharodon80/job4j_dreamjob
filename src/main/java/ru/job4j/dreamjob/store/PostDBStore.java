package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostDBStore {
    private final BasicDataSource pool;
    private static final Logger LOG = LogManager.getLogger(PostDBStore.class.getName());
    private static final String FIND_ALL_POSTS = "SELECT * FROM post ORDER BY id";
    private static final String INSERT_POST = "INSERT INTO post(name, city_id, description, date, visible, file_id) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_POST = "UPDATE post SET (name, city_id, description, visible, file_id)"
            + " = (?, ?, ?, ?, ?) WHERE id = ?";
    private static final String SELECT_POST = "SELECT * FROM post WHERE id = ?";
    private static final String DELETE_POST = "DELETE FROM post WHERE id = ?";

    public PostDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_POSTS)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(createPost(it));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return posts;
    }

    public Post add(Post post) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT_POST, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getCity().getId());
            ps.setString(3, post.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.setBoolean(5, post.isVisible());
            ps.setInt(6, post.getFileId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return post;
    }

    public boolean update(Post post) {
        boolean isUpdated = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_POST)
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getCity().getId());
            ps.setString(3, post.getDescription());
            ps.setBoolean(4, post.isVisible());
            ps.setInt(5, post.getFileId());
            ps.setInt(6, post.getId());
            isUpdated = ps.executeUpdate() != 0;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return isUpdated;
    }

    public Post findById(int id) {
        Post post = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_POST)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    post = createPost(it);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return post;
    }

    public boolean deletePost(int id) {
        boolean postIsDeleted = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_POST)
        ) {
            ps.setInt(1, id);
            postIsDeleted = ps.executeUpdate() != 0;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return postIsDeleted;
    }

    private Post createPost(ResultSet it) throws SQLException {
        Post post = new Post(it.getInt("id"), it.getString("name"));
        post.setDescription(it.getString("description"));
        post.setCreated(it.getTimestamp("date").toLocalDateTime());
        post.setVisible(it.getBoolean("visible"));
        post.setCity(new City(it.getInt("city_id"), ""));
        post.setFileId(it.getInt("file_id"));
        return post;
    }
}
