package br.com.fiap.processador_video.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.application.service.ArquivoTemporarioService;
import br.com.fiap.processador_video.application.service.ExtratorFrameService;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.AtualizarVideoUseCase;
import br.com.fiap.processador_video.domain.usecase.RegistrarVideoUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class ProcessarVideoUseCaseImplTest {

    private ExtratorFrameService extratorFrameService;
    private ArquivoTemporarioService arquivoTemporarioService;
    private RegistrarVideoUseCase registrarVideoUseCase;
    private AtualizarVideoUseCase atualizarVideoUseCase;
    private ProcessarVideoUseCaseImpl useCase;

    @BeforeEach
    void setUp() throws Exception {
        extratorFrameService = mock(ExtratorFrameService.class);
        arquivoTemporarioService = mock(ArquivoTemporarioService.class);
        registrarVideoUseCase = mock(RegistrarVideoUseCase.class);
        atualizarVideoUseCase = mock(AtualizarVideoUseCase.class);

        useCase = new ProcessarVideoUseCaseImpl(extratorFrameService, arquivoTemporarioService, registrarVideoUseCase, atualizarVideoUseCase);

        // Injeta o valor do tempDirectory (se necessário)
        var field = ProcessarVideoUseCaseImpl.class.getDeclaredField("tempDirectory");
        field.setAccessible(true);
        field.set(useCase, "videos-temp-test");
    }

    @Test
    void deveAtualizarStatusParaConcluidoQuandoProcessarComMaisDe90PorCento() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[10]);
        Video video = new Video();
        UUID videoId = UUID.randomUUID();
        video.setId(videoId);

        when(registrarVideoUseCase.registrar(file)).thenReturn(video);
        when(arquivoTemporarioService.salvarVideoTemporario(eq(file), anyString()))
                .thenReturn(Paths.get("video-temp/video.mp4"));
        when(extratorFrameService.contarFramesEsperadosPorDuracao(any()))
                .thenReturn(10);
        when(extratorFrameService.extrairFrames(any(), any(), any()))
                .thenReturn(10);

        // Act
        useCase.executar(file);
        Thread.sleep(500); // necessário por ser @Async

        // Assert
        verify(atualizarVideoUseCase).atualizar(videoId, VideoStatus.CONCLUIDO);
        verify(arquivoTemporarioService).deletarArquivo(any());
    }

    @Test
    void deveAtualizarStatusParaErroQuandoProcessamentoForInferiorA90PorCento() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[10]);
        Video video = new Video();
        UUID videoId = UUID.randomUUID();
        video.setId(videoId);

        when(registrarVideoUseCase.registrar(file)).thenReturn(video);
        when(arquivoTemporarioService.salvarVideoTemporario(eq(file), anyString()))
                .thenReturn(Paths.get("video-temp/video.mp4"));
        when(extratorFrameService.contarFramesEsperadosPorDuracao(any()))
                .thenReturn(10);
        when(extratorFrameService.extrairFrames(any(), any(), any()))
                .thenReturn(5); // 50%

        useCase.executar(file);
        Thread.sleep(500);

        verify(atualizarVideoUseCase).atualizar(videoId, VideoStatus.ERRO);
    }

    @Test
    void deveAtualizarStatusParaErroQuandoLancarExcecaoDuranteProcessamento() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[10]);
        Video video = new Video();
        UUID videoId = UUID.randomUUID();
        video.setId(videoId);

        when(registrarVideoUseCase.registrar(file)).thenReturn(video);
        when(arquivoTemporarioService.salvarVideoTemporario(eq(file), anyString()))
                .thenThrow(new RuntimeException("Erro ao salvar"));

        useCase.executar(file);
        Thread.sleep(500);

        verify(atualizarVideoUseCase).atualizar(videoId, VideoStatus.ERRO);
    }

    @Test
    void naoDeveAtualizarStatusQuandoErroAcontecerAntesDeRegistrarVideo() {
        MultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[10]);

        when(registrarVideoUseCase.registrar(file))
                .thenThrow(new RuntimeException("Erro ao registrar"));

        useCase.executar(file);
        verifyNoInteractions(atualizarVideoUseCase);
    }
}
