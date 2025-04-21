package br.com.fiap.processador_video.application.usecase;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.application.gateway.ArmazenamentoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;

class DownloadZipUseCaseImplTest {

    private BuscarVideoPorIdUseCase buscarVideoPorIdUseCase;
    private ArmazenamentoGateway armazenamentoGateway;
    private DownloadZipUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        buscarVideoPorIdUseCase = mock(BuscarVideoPorIdUseCase.class);
        armazenamentoGateway = mock(ArmazenamentoGateway.class);
        useCase = new DownloadZipUseCaseImpl(buscarVideoPorIdUseCase, armazenamentoGateway);
    }

    @Test
    void deveBaixarZipComSucesso() throws VideoNotFoundException, IllegalStateException, IOException {
        // Arrange
        UUID videoId = UUID.randomUUID();
        String usuarioId = UUID.randomUUID().toString();
        Video video = new Video();
        video.setZipPath("video123/zip/video.zip");

        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = new S3ObjectInputStream(
                new ByteArrayInputStream("conteudo do zip".getBytes()), null
        );

        when(buscarVideoPorIdUseCase.buscar(videoId)).thenReturn(video);
        when(armazenamentoGateway.buscarObjeto(video.getZipPath())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);

        // Act
        InputStreamResource resource = useCase.baixarZip(videoId, usuarioId);

        // Assert
        assertNotNull(resource);
        assertSame(inputStream, resource.getInputStream());
    }

    @Test
    void deveLancarExcecaoQuandoVideoNaoForEncontrado() throws VideoNotFoundException {
        // Arrange
        UUID videoId = UUID.randomUUID();
        String usuarioId = UUID.randomUUID().toString();
        when(buscarVideoPorIdUseCase.buscar(videoId)).thenThrow(new VideoNotFoundException(videoId));

        // Act & Assert
        assertThrows(VideoNotFoundException.class, () -> useCase.baixarZip(videoId, usuarioId));
    }
}
