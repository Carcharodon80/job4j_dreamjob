package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CandidateDBStore {
    private final BasicDataSource pool;
    private static final Logger LOG = LogManager.getLogger(CandidateDBStore.class.getName());
    private static final String SELECT_ALL_CANDIDATES = "SELECT * FROM candidates ORDER BY id";
    private static final String INSERT_CANDIDATE = "INSERT INTO candidates(name, description, date, photo)"
            + "values (?, ?, ?, ?)";
    private static final String SELECT_CANDIDATE = "SELECT * FROM candidates WHERE id = ?";
    private static final String UPDATE_CANDIDATE = "UPDATE candidates SET (name, description, date, photo)"
            + " = (?, ?, ?, ?) WHERE id = ?";

    public CandidateDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL_CANDIDATES)) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(createCandidate(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
        return candidates;
    }

    public Candidate add(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT_CANDIDATE, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(candidate.getCreated()));
            ps.setBytes(4, candidate.getPhoto());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
        return candidate;
    }

    public Candidate findById(int id) {
        Candidate candidate = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_CANDIDATE)) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    candidate = createCandidate(it);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
        return candidate;
    }

    public boolean update(Candidate candidate) {
        boolean result = false;
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement(UPDATE_CANDIDATE)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(candidate.getCreated()));
            ps.setBytes(4, candidate.getPhoto());
            ps.setInt(5, candidate.getId());
            ps.executeUpdate();
            result = true;
        } catch (Exception e) {
            LOG.error("Exception: ", e);
        }
        return result;
    }

    private Candidate createCandidate(ResultSet it) throws SQLException {
        Candidate candidate = new Candidate(it.getInt("id"), it.getString("name"));
        candidate.setDescription(it.getString("description"));
        candidate.setCreated(it.getTimestamp("date").toLocalDateTime());
        candidate.setPhoto(it.getBytes("photo"));
        return candidate;
    }
}
