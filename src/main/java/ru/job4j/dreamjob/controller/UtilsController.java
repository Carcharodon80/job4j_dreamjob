package ru.job4j.dreamjob.controller;

import ru.job4j.dreamjob.model.User;

import javax.servlet.http.HttpSession;

public class UtilsController {
    public static User getUserFromSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        return user;
    }
}
