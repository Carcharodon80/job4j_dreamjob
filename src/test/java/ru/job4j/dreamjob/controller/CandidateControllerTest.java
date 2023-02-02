package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class CandidateControllerTest {
    private CandidateService candidateService;
    private CandidateController candidateController;
    private HttpSession session;
    private Candidate testCandidate;
    private MultipartFile testFile;

    @BeforeEach
    public void init() {
        candidateService = mock(CandidateService.class);
        candidateController = new CandidateController(candidateService);
        session = mock(HttpSession.class);
        testCandidate = new Candidate(1, "testCandidate", "description", now());
        testFile = new MockMultipartFile("testPhoto.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenCandidatesPageReturnCandidatesPage() {
        Candidate testCandidate2 = new Candidate(2, "testCandidate_2", "description_2", now());
        List<Candidate> exceptedCandidates = List.of(testCandidate, testCandidate2);
        when(candidateService.findAll()).thenReturn(exceptedCandidates);
        Model model = new ConcurrentModel();
        String view = candidateController.candidates(model, session);
        assertThat(model.getAttribute("candidates")).isEqualTo(exceptedCandidates);
        assertThat(view).isEqualTo("candidates");
    }

    @Test
    public void whenFormAddCandidateReturnAddCandidate() {
        Model model = new ConcurrentModel();
        String view = candidateController.addCandidate(model, session);
        assertThat(view).isEqualTo("addCandidate");
    }

    @Test
    public void whenCreateCandidateRedirectCandidatesPage() throws IOException {
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        String view = candidateController.createCandidate(testCandidate, testFile);
        verify(candidateService).add(candidateArgumentCaptor.capture());
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(candidateArgumentCaptor.getValue()).isEqualTo(testCandidate);
    }

    @Test
    public void whenFormUpdateCandidateReturnUpdateCandidate() {
        Model model = new ConcurrentModel();
        String view = candidateController.formUpdateCandidate(model, testCandidate.getId(), session);
        assertThat(view).isEqualTo("updateCandidate");
    }

    @Test
    public void whenUpdateCandidateRedirectCandidatesPage() throws IOException {
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        String view = candidateController.updateCandidate(testCandidate, testFile);
        verify(candidateService).update(candidateArgumentCaptor.capture());
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(candidateArgumentCaptor.getValue()).isEqualTo(testCandidate);
    }

    @Test
    public void whenDownloadPhotoCandidateThenGetPhoto() throws IOException {
        when(candidateService.findById(testCandidate.getId())).thenReturn(testCandidate);
        testCandidate.setPhoto(testFile.getBytes());
        ByteArrayResource arrayPhoto = (ByteArrayResource) candidateController.download(testCandidate.getId()).getBody();
        assertThat(arrayPhoto.getByteArray()).isEqualTo(testFile.getBytes());
    }

    @Test
    public void whenDeleteCandidateThenRedirectCandidatesPage() {
        when(candidateService.delete(testCandidate.getId())).thenReturn(true);
        String view = candidateController.deleteCandidate(testCandidate.getId());
        assertThat(view).isEqualTo("redirect:/candidates");
    }
}