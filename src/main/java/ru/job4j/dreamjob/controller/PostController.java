package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.PostService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@ThreadSafe
@Controller
public class PostController {
    private final PostService postService;
    private final CityService cityService;

    public PostController(PostService postService, CityService cityService) {
        this.postService = postService;
        this.cityService = cityService;
    }

    @GetMapping("/posts")
    public String posts(Model model, HttpSession session) {
        model.addAttribute("posts", postService.findAll());
        model.addAttribute("cities", cityService.getAllCities());
        model.addAttribute("user", getUserFromSession(session));
        return "posts";
    }

    @GetMapping("/formAddPost")
    public String addPost(Model model, HttpSession session) {
        model.addAttribute("post", new Post(0, "Заполните поле"));
        model.addAttribute("cities", cityService.getAllCities());
        model.addAttribute("user", getUserFromSession(session));
        return "addPost";
    }

    /**
     * ModelAttribute - показывает, что надо собрать post из полученных данных
     * Выпадающий список (тэг select на форме) может передать только id,
     * по этому id находим город и добавляем его в post
     */
    @PostMapping("/createPost")
    public String createPost(@ModelAttribute Post post) {
        City city = cityService.findById(post.getCity().getId());
        post.setCity(city);
        post.setCreated(LocalDateTime.now());
        postService.add(post);
        return "redirect:/posts";
    }

    @GetMapping("/formUpdatePost/{postId}")
    public String formUpdatePost(Model model, @PathVariable("postId") int id, HttpSession session) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("cities", cityService.getAllCities());
        model.addAttribute("user", getUserFromSession(session));
        return "updatePost";
    }

    /**
     * Выпадающий список (тэг select на форме) может передать только id,
     * по этому id находим город и обновляем в списке вакансий
     */
    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute Post post) {
        City city = cityService.findById(post.getCity().getId());
        post.setCity(city);
        postService.update(post);
        return "redirect:/posts";
    }

    private User getUserFromSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        return user;
    }
}
