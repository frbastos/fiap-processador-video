package br.com.fiap.processador_video.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;

class ListarVideosProcessadosUseCaseImplTest {

    private VideoGateway videoGateway;
    private ListarVideosProcessadosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        videoGateway = mock(VideoGateway.class);
        useCase = new ListarVideosProcessadosUseCaseImpl(videoGateway);
    }

    @Test
    void deveListarVideosComSucesso() {
        // Arrange
        Video video1 = new Video();
        Video video2 = new Video();
        List<Video> mockVideos = Arrays.asList(video1, video2);

        when(videoGateway.listarVideos()).thenReturn(mockVideos);

        // Act
        List<Video> resultado = useCase.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(videoGateway).listarVideos();
    }
}
