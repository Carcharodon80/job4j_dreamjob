package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;
import ru.job4j.dreamjob.util.UtilsController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserService userService;
    private HttpSession session;
    private User testUser;
    private UserController userController;
    private HttpServletRequest request;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        session = mock(HttpSession.class);
        testUser = new User(1, "TestUserName");
        userController = new UserController(userService);
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void whenLoginPageThenReturnLogin() {
        Model model = new ConcurrentModel();
        when(UtilsController.getUserFromSession(session)).thenReturn(testUser);
        String view = userController.loginPage(model, false, session);
        assertThat(model.getAttribute("fail")).isEqualTo(true);
        assertThat(model.getAttribute("user")).isEqualTo(testUser);
        assertThat(view).isEqualTo("login");
    }

    @Test
    public void whenLoginFailThenRedirectLoginPageFail() {
        when(userService.findUserByEmailAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
        String view = userController.login(testUser, request);
        assertThat(view).isEqualTo("redirect:/loginPage?fail=true");
    }

    @Test
    public void whenLoginSuccessThenRedirectIndex() {
        when(userService.findUserByEmailAndPassword(testUser.getName(), testUser.getPassword())).
                thenReturn(Optional.of(testUser));

        when(request.getSession()).thenReturn(session);
        String view = userController.login(testUser, request);
        assertThat(view).isEqualTo("redirect:/index");
    }

    @Test
    public void whenAddUserThenReturnAddUser() {
        Model model = new ConcurrentModel();
        String view = userController.addUser(model, session);
        assertThat(view).isEqualTo("addUser");
    }

    @Test
    public void whenRegistrationFailThenRedirectFail() {
        when(userService.add(testUser)).thenReturn(Optional.empty());
        String view = userController.registration(testUser);
        assertThat(view).isEqualTo("redirect:/fail");
    }

    @Test
    public void whenRegistrationSuccessThenRedirectSuccess() {
        when(userService.add(testUser)).thenReturn(Optional.of(testUser));
        String view = userController.registration(testUser);
        assertThat(view).isEqualTo("redirect:/success");
    }
}