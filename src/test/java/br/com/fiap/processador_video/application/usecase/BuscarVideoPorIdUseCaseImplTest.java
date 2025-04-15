package br.com.fiap.processador_video.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;

class BuscarVideoPorIdUseCaseImplTest {

    private VideoGateway videoGateway;
    private BuscarVideoPorIdUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        videoGateway = mock(VideoGateway.class);
        useCase = new BuscarVideoPorIdUseCaseImpl(videoGateway);
    }

    @Test
    void deveRetornarVideoQuandoEncontrado() throws VideoNotFoundException {
        // Arrange
        UUID videoId = UUID.randomUUID();
        Video video = mock(Video.class);
        when(videoGateway.buscarPorId(videoId)).thenReturn(Optional.of(video));

        // Act
        Video resultado = useCase.buscar(videoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(video, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoVideoNaoEncontrado() {
        // Arrange
        UUID videoId = UUID.randomUUID();
        when(videoGateway.buscarPorId(videoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VideoNotFoundException.class, () -> useCase.buscar(videoId));
    }
}
