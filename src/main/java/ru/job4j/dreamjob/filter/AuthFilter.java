package ru.job4j.dreamjob.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class AuthFilter implements Filter {
    /**
     * Неизменяемый набор валидных маппингов для фильтра
     */
    private static final Set<String> MAPPINGS = Set.of("loginPage", "login", "formAddUser", "registration",
            "success", "fail", "posts", "candidates", "index", "photoCandidate");

    /**
     * Сервлетный фильтр, пропускает неавторизированного пользователя на все страницы,
     * указанные в mappings (см. isValidMapping), туда не входят -
     * "Добавить вакансию", "Добавить кандидата" и "Обновить вакансию", "Обновить кандидата"
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if (isValidMapping(uri)) {
            chain.doFilter(req, res);
            return;
        }
        if (req.getSession().getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/loginPage");
            return;
        }
        chain.doFilter(req, res);
    }

    private boolean isValidMapping(String uri) {
        return MAPPINGS.stream().anyMatch(uri::endsWith);
    }
}
