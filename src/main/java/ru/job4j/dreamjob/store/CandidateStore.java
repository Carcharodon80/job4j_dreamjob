package ru.job4j.dreamjob.store;

import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CandidateStore {
    private static final CandidateStore INST = new CandidateStore();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger(0);

    private CandidateStore() {
        this.add(new Candidate(1, "Junior", "John Smith",
                LocalDateTime.of(2021, Month.DECEMBER, 23, 15, 45)));
        this.add(new Candidate(2, "Senior", "Jane Dow",
                LocalDateTime.of(2020, Month.JANUARY, 1, 5, 4)));
    }

    public static CandidateStore instOf() {
        return INST;
    }

    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    public void add(Candidate candidate) {
        candidate.setId(id.intValue());
        candidates.put(candidate.getId(), candidate);
        id.incrementAndGet();
    }

    public Candidate findById(int id) {
        return candidates.get(id);
    }

    public void update(Candidate candidate) {
        candidates.replace(candidate.getId(), candidate);
    }
}
