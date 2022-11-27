package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.Candidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateDBStoreTest {
    private static CandidateDBStore store;
    private static BasicDataSource pool;
    private static final String DELETE_ALL = "DELETE FROM candidates";

    @BeforeAll
    public static void createStore() {
        pool = new Main().loadPool();
        store = new CandidateDBStore(pool);
    }

    /**
     * Если не очищать store - не проходит findAll()
     */
    @BeforeEach
    public void clearStore() {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE_ALL)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenAddCandidate() {
        Candidate candidate = new Candidate(0, "John", "Worker", LocalDateTime.now());
        store.add(candidate);
        Candidate candidateInDb = store.findById(candidate.getId());
        assertEquals(candidate.getName(), candidateInDb.getName());
    }

    @Test
    public void whenFindById() {
        Candidate candidate = new Candidate(0, "John", "Worker", LocalDateTime.now());
        store.add(candidate);
        Candidate candidateInDb = store.findById(candidate.getId());
        assertEquals(candidate, candidateInDb);
    }

    @Test
    public void whenUpdate() {
        Candidate candidate = new Candidate(0, "John", "Worker", LocalDateTime.now());
        store.add(candidate);
        candidate.setName("New John");
        store.update(candidate);
        Candidate candidateInDb = store.findById(candidate.getId());
        assertEquals(candidate.getName(), candidateInDb.getName());
    }

    @Test
    public void whenFindAll() {
        Candidate candidate = new Candidate(0, "John", "Worker", LocalDateTime.now());
        Candidate candidate1 = new Candidate(0, "Mary", "Secretary", LocalDateTime.now());
        List<Candidate> candidates = Arrays.asList(candidate, candidate1);
        store.add(candidate);
        store.add(candidate1);
        assertEquals(candidates, store.findAll());
    }
}