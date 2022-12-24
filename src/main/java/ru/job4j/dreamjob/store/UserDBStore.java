package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserDBStore {
    private final BasicDataSource pool;
    private static final Logger LOG = LogManager.getLogger(UserDBStore.class.getName());
    private static final String INSERT_USER = "INSERT INTO users(email, password) values (?, ?)";
    private static final String SELECT_USER = "SELECT * FROM users WHERE email = ? AND password = ?";

    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Integer> add(User user) {
        Optional<Integer> optional = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            optional = Optional.of(ps.executeUpdate());
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return optional;
    }

    public Optional<User> selectUserByEmailAndPassword(String email, String password) {
        Optional<User> optional = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_USER)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                User user = new User(resultSet.getInt("id"), resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                optional = Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return optional;
    }
}
