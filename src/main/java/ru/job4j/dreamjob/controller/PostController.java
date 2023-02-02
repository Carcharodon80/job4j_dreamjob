package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.PostService;
import ru.job4j.dreamjob.util.UtilsController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
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
        model.addAttribute("user", UtilsController.getUserFromSession(session));
        return "posts";
    }

    @GetMapping("/formAddPost")
    public String addPost(Model model, HttpSession session) {
        model.addAttribute("post", new Post(0, "Заполните поле"));
        model.addAttribute("cities", cityService.getAllCities());
        model.addAttribute("user", UtilsController.getUserFromSession(session));
        return "addPost";
    }

    /**
     * ModelAttribute - показывает, что надо собрать post из полученных данных
     * Выпадающий список (тэг select на форме) может передать только id,
     * по этому id находим город и добавляем его в post
     */
    @PostMapping("/createPost")
    public String createPost(@ModelAttribute Post post, @RequestParam MultipartFile file, Model model) {
        City city = cityService.findById(post.getCity().getId());
        post.setCity(city);
        post.setCreated(LocalDateTime.now());
        try {
            postService.add(post, new FileDto(file.getOriginalFilename(), file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/posts";
    }

    @GetMapping("/formUpdatePost/{postId}")
    public String formUpdatePost(Model model, @PathVariable("postId") int id, HttpSession session) {
        model.addAttribute("post", postService.findById(id).get());
        model.addAttribute("cities", cityService.getAllCities());
        model.addAttribute("user", UtilsController.getUserFromSession(session));
        return "updatePost";
    }

    /**
     * Выпадающий список (тэг select на форме) может передать только id,
     * по этому id находим город и обновляем в списке вакансий
     */
    @PostMapping("/updatePost")
    public String updatePost(@ModelAttribute Post post, @RequestParam MultipartFile file, Model model) {
        City city = cityService.findById(post.getCity().getId());
        post.setCity(city);
        try {
            postService.update(post, new FileDto(file.getOriginalFilename(), file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/posts";
    }

    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable int id, Model model) {
        if (!postService.delete(id)) {
            return "redirect:/postNotDeleted";
        }
        return "redirect:/posts";
    }

    @GetMapping("/postNotDeleted")
    public String deletePostFail(Model model) {
        model.addAttribute("message", "Вакансия с указанным идентификатором не найдена.");
        return "message";
    }
}
