package br.com.fiap.processador_video.application.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.fiap.processador_video.application.gateway.PublicarStatusVideoGateway;
import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.event.StatusVideoEvent;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class AtualizarVideoUseCaseImplTest {

    private VideoGateway videoGateway;
    private PublicarStatusVideoGateway publicarStatusVideoGateway;
    private BuscarVideoPorIdUseCase buscarVideoPorIdUseCase;
    private AtualizarVideoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        videoGateway = mock(VideoGateway.class);
        publicarStatusVideoGateway = mock(PublicarStatusVideoGateway.class);
        buscarVideoPorIdUseCase = mock(BuscarVideoPorIdUseCase.class);
        useCase = new AtualizarVideoUseCaseImpl(videoGateway, publicarStatusVideoGateway, buscarVideoPorIdUseCase);
    }

    @Test
    void deveAtualizarStatusParaConcluidoComSucesso() throws Exception {
        // Arrange
        UUID videoId = UUID.randomUUID();

        Video video = mock(Video.class);
        when(buscarVideoPorIdUseCase.buscar(videoId)).thenReturn(video);

        // Act
        useCase.atualizar(videoId, VideoStatus.CONCLUIDO);

        // Assert
        verify(video).concluido();
        verify(videoGateway).salvar(video);
        verify(publicarStatusVideoGateway).publicar(new StatusVideoEvent(videoId, VideoStatus.CONCLUIDO));
    }

    @Test
    void deveLancarRuntimeExceptionSeFalharAoPublicarEvento() throws Exception {
        // Arrange
        UUID videoId = UUID.randomUUID();
        Video video = mock(Video.class);
        when(buscarVideoPorIdUseCase.buscar(videoId)).thenReturn(video);
        doThrow(new JsonProcessingException("erro") {}).when(publicarStatusVideoGateway)
                .publicar(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.atualizar(videoId, VideoStatus.CONCLUIDO));

        verify(video).concluido();
        verify(videoGateway).salvar(video);
    }

    @Test
    void deveAtualizarZipPathComSucesso() throws Exception {
        // Arrange
        UUID videoId = UUID.randomUUID();
        String zipPath = "videos/abc123.zip";
        Video video = mock(Video.class);
        when(buscarVideoPorIdUseCase.buscar(videoId)).thenReturn(video);

        // Act
        useCase.atualizarZipPath(videoId, zipPath);

        // Assert
        verify(video).setZipPath(zipPath);
        verify(videoGateway).salvar(video);
    }
}

