package br.com.fiap.processador_video.infra.listener;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.event.ZipGeradoEvent;
import br.com.fiap.processador_video.domain.usecase.AtualizarVideoUseCase;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class ZipGeradoListenerTest {

    private BuscarVideoPorIdUseCase buscarVideoPorIdUseCase;
    private AtualizarVideoUseCase atualizarVideoUseCase;
    private ZipGeradoListener listener;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        buscarVideoPorIdUseCase = mock(BuscarVideoPorIdUseCase.class);
        atualizarVideoUseCase = mock(AtualizarVideoUseCase.class);
        listener = new ZipGeradoListener(buscarVideoPorIdUseCase, atualizarVideoUseCase);
        mapper = new ObjectMapper();
    }

    @Test
    void deveAtualizarZipQuandoVideoEncontradoEValido() throws Exception {
        // Arrange
        UUID videoId = UUID.randomUUID();
        String zipPath = "videos/123.zip";

        String usuarioId = UUID.randomUUID().toString();

        Video video = new Video(videoId, "video.mp4", VideoStatus.PROCESSANDO, null, usuarioId);
        when(buscarVideoPorIdUseCase.buscar(videoId)).thenReturn(video);

        ZipGeradoEvent event = new ZipGeradoEvent(videoId.toString(), zipPath);
        String json = mapper.writeValueAsString(event);

        // Act
        listener.receberStatusVideo(json);

        // Assert
        verify(atualizarVideoUseCase).atualizarZipPath(videoId, zipPath);
    }

    @Test
    void naoDeveAtualizarZipQuandoStatusConcluidoOuZipPathJaExiste() throws Exception {
        UUID videoId = UUID.randomUUID();
        String zipPath = "videos/123.zip";

        String usuarioId = UUID.randomUUID().toString();

        // vídeo já concluído e com zip
        Video video = new Video(videoId, "video.mp4", VideoStatus.PROCESSANDO, zipPath, usuarioId);
        when(buscarVideoPorIdUseCase.buscar(videoId)).thenReturn(video);

        ZipGeradoEvent event = new ZipGeradoEvent(videoId.toString(), zipPath);
        String json = mapper.writeValueAsString(event);

        listener.receberStatusVideo(json);

        verify(atualizarVideoUseCase, never()).atualizarZipPath(any(), any());
    }

    @Test
    void deveIgnorarQuandoVideoNaoForEncontrado() throws Exception {
        UUID videoId = UUID.randomUUID();
        String zipPath = "videos/abc.zip";

        when(buscarVideoPorIdUseCase.buscar(videoId)).thenThrow(new VideoNotFoundException(videoId));

        ZipGeradoEvent event = new ZipGeradoEvent(videoId.toString(), zipPath);
        String json = mapper.writeValueAsString(event);

        listener.receberStatusVideo(json);

        verify(atualizarVideoUseCase, never()).atualizarZipPath(any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoJsonForInvalido() {
        String jsonInvalido = "{ \"videoId\": \"sem-fechamento\" ";

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            listener.receberStatusVideo(jsonInvalido);
        });

        assertTrue(ex.getMessage().contains("Erro ao receber mensagem zip gerado"));
    }
}
