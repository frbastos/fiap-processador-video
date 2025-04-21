package br.com.fiap.processador_video.infra.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.DownloadZipUseCase;
import br.com.fiap.processador_video.domain.usecase.ListarVideosProcessadosUseCase;
import br.com.fiap.processador_video.domain.usecase.ProcessarVideoUseCase;
import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class VideoControllerTest {

    @Mock
    private ProcessarVideoUseCase processarVideoUseCase;

    @Mock
    private ListarVideosProcessadosUseCase listarVideosProcessadosUseCase;

    @Mock
    private DownloadZipUseCase downloadZipUseCase;

    private VideoController videoController;
    private MockMvc mockMvc;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        videoController = new VideoController(processarVideoUseCase, listarVideosProcessadosUseCase,
                downloadZipUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(videoController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void deveAceitarUploadDeVideos() throws Exception {
        String usuarioId = UUID.randomUUID().toString();
        UsuarioContext.setUsuarioId(usuarioId);
        
        MockMultipartFile file = new MockMultipartFile("files", "video.mp4", "video/mp4", "conteudo".getBytes());

        mockMvc.perform(multipart("/videos/upload").file(file))
                .andExpect(status().isAccepted());

        verify(processarVideoUseCase).executar(any(), any());
    }

    @Test
    void deveListarVideosProcessados() throws Exception {
        String usuarioId = UUID.randomUUID().toString();
        UsuarioContext.setUsuarioId(usuarioId);

        Video video1 = new Video(UUID.randomUUID(), "video1.mp4", VideoStatus.CONCLUIDO, "path1", usuarioId);
        Video video2 = new Video(UUID.randomUUID(), "video2.mp4", VideoStatus.ERRO, "path2", usuarioId);

        when(listarVideosProcessadosUseCase.listar(any())).thenReturn(List.of(video1, video2));

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void deveFazerDownloadDoZip() throws Exception {
        UUID videoId = UUID.randomUUID();

        String usuarioId = UUID.randomUUID().toString();
        UsuarioContext.setUsuarioId(usuarioId);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream("zip content".getBytes()));

        when(downloadZipUseCase.baixarZip(videoId, usuarioId)).thenReturn(resource);

        mockMvc.perform(get("/videos/{id}/download", videoId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"video.zip\""));
    }

    @Test
    void deveRetornar404QuandoVideoNaoExistirNoDownload() throws Exception {
        UUID videoId = UUID.randomUUID();

        String usuarioId = UUID.randomUUID().toString();
        UsuarioContext.setUsuarioId(usuarioId);

        when(downloadZipUseCase.baixarZip(videoId, usuarioId)).thenThrow(new VideoNotFoundException(videoId));

        mockMvc.perform(get("/videos/{id}/download", videoId))
                .andExpect(status().isNotFound());
    }
}
