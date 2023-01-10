package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.dreamjob.util.UtilsController;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
public class IndexController {
    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        model.addAttribute("user", UtilsController.getUserFromSession(session));
        return "index";
    }
}
