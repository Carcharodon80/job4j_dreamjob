package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.PostService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class PostControllerTest {
    private PostService postService;
    private CityService cityService;
    private PostController postController;
    private MultipartFile testFile;
    private HttpSession session;
    private City city;
    private Post testPost;

    @BeforeEach
    public void initServices() {
        postService = mock(PostService.class);
        cityService = mock(CityService.class);
        postController = new PostController(postService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
        session = mock(HttpSession.class);
        city = new City(1, "NY");
        testPost = new Post(1, "test1", "desc1", now(), true, city, 2);
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

    /**
     * С помощью ArgumentCaptor проверяем, что передается в postService
     */
    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        FileDto fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(postService.add(postArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(testPost);

        Model model = new ConcurrentModel();
        String view = postController.createPost(testPost, testFile, model);
        Post actualPost = postArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/posts");
        assertThat(actualPost).isEqualTo(testPost);
        assertThat(actualFileDto).isEqualTo(fileDto);
    }

    @Test
    public void whenFormUpdatePostThenRedirectToUpdatePostPage() throws IOException {
        FileDto fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(postService.update(testPost, fileDto)).thenReturn(true);
        when(postService.findById(testPost.getId())).thenReturn(Optional.of(testPost));
        Model model = new ConcurrentModel();
        String view = postController.formUpdatePost(model, testPost.getId(), session);
        assertThat(view).isEqualTo("updatePost");
    }

    @Test
    public void whenUpdatePostThenSameDataAndRedirectToVacanciesPage() throws IOException {
        FileDto fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(postService.update(postArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        Model model = new ConcurrentModel();
        String view = postController.updatePost(testPost, testFile, model);
        Post actualPost = postArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/posts");
        assertThat(actualPost).isEqualTo(testPost);
        assertThat(actualFileDto).isEqualTo(fileDto);

    }

    @Test
    public void whenPostNotDeletedThenGetPageWithMessage() {
        when(postService.delete(0)).thenReturn(false);
        Model model = new ConcurrentModel();
        String view = postController.deletePost(0, model);
        assertThat(view).
                isEqualTo("redirect:/postNotDeleted");

    }

}
