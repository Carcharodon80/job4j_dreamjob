package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.PostService;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class PostControllerTest {
    private PostService postService;
    private CityService cityService;
    private PostController postController;
    private MultipartFile testFile;
    private HttpSession session;

    @BeforeEach
    public void initServices() {
        postService = mock(PostService.class);
        cityService = mock(CityService.class);
        postController = new PostController(postService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
        session = mock(HttpSession.class);
    }

    /**
     * Когда postController создает страницу с вакансиями, он берет вакансии из postService -
     * работу postService симулируем с помощью Mockito (when...thenReturn).
     * Сравниваем: 1 - что postController вернул нужную страницу, 2 - postController правильно
     * передал то, что получил из postService.
     */
    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var post1 = new Post(1, "test1");
        var post2 = new Post(2, "test2");
        var expectedPosts = List.of(post1, post2);
        when(postService.findAll()).thenReturn(expectedPosts);
        var model = new ConcurrentModel();
        var view = postController.posts(model, session);
        var actualPosts = model.getAttribute("posts");
        assertThat(view).isEqualTo("posts");
        assertThat(actualPosts).isEqualTo(expectedPosts);
    }

    /**
     * Аналогично предыдущему, но проверяем, что при создании вакансии в модель правильно передались города.
     */
    @Test
    public void whenRequestPostCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Spb");
        var expectedCities = List.of(city1, city2);
        when(cityService.getAllCities()).thenReturn(expectedCities);
        var model = new ConcurrentModel();
        var view = postController.addPost(model, session);
        var actualCities = model.getAttribute("cities");
        assertThat(view).isEqualTo("addPost");
        assertThat(actualCities).isEqualTo(expectedCities);
    }
}
