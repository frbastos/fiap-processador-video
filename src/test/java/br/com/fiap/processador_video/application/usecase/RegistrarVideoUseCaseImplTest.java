package br.com.fiap.processador_video.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class RegistrarVideoUseCaseImplTest {

    private VideoGateway videoGateway;
    private RegistrarVideoUseCaseImpl registrarVideoUseCase;

    @BeforeEach
    void setUp() {
        videoGateway = mock(VideoGateway.class);
        registrarVideoUseCase = new RegistrarVideoUseCaseImpl(videoGateway);
    }

    @Test
    void deveRegistrarVideoComSucesso() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "meuvideo.mp4", "video/mp4", new byte[10]);

        // Act
        Video video = registrarVideoUseCase.registrar(file);

        // Assert
        assertNotNull(video);
        assertEquals("meuvideo.mp4", video.getNomeOriginal());
        assertEquals(VideoStatus.PROCESSANDO, video.getStatus());
        verify(videoGateway).salvar(video);
    }

    @Test
    void deveLancarExcecaoQuandoAmqpFalhar() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", new byte[10]);

        doThrow(new AmqpException("Falha na fila")).when(videoGateway).salvar(any());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> registrarVideoUseCase.registrar(file));
        assertTrue(ex.getMessage().contains("Erro ao publicar evento"));
    }
}
