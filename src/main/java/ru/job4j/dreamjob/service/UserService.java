package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.store.UserDBStore;

import java.util.Optional;

@ThreadSafe
@Service
public class UserService {
    private final UserDBStore userDBStore;

    public UserService(UserDBStore userDBStore) {
        this.userDBStore = userDBStore;
    }

    public Optional<Integer> add(User user) {
        return userDBStore.add(user);
    }

    public Optional<User> findUserByEmailAndPassword(String email, String password) {
        return userDBStore.selectUserByEmailAndPassword(email, password);
    }
}
