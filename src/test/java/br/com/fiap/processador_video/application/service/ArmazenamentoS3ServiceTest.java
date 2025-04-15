package br.com.fiap.processador_video.application.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

class ArmazenamentoS3ServiceTest {

    private AmazonS3 amazonS3;
    private ArmazenamentoS3Service armazenamentoS3Service;

    @BeforeEach
    void setUp() throws Exception {
        amazonS3 = mock(AmazonS3.class);
        armazenamentoS3Service = new ArmazenamentoS3Service(amazonS3);

        // Injeta o valor do bucket via reflection
        var field = ArmazenamentoS3Service.class.getDeclaredField("bucket");
        field.setAccessible(true);
        field.set(armazenamentoS3Service, "bucket-test");
    }

    @Test
    void deveFazerUploadParaOS3ComSucesso() {
        // Arrange
        Path localPath = Paths.get("test.txt"); // apenas referência, não precisa existir no teste
        String key = "videos/test.txt";

        // Act
        String result = armazenamentoS3Service.upload(localPath, key);

        // Assert
        assertEquals(key, result);
        verify(amazonS3).putObject("bucket-test", key, localPath.toFile());
    }

    @Test
    void deveLancarExcecaoQuandoAmazonS3Falhar() {
        // Arrange
        Path localPath = Paths.get("test.txt");
        String key = "videos/test.txt";

        doThrow(new AmazonServiceException("Erro")).when(amazonS3)
                .putObject("bucket-test", key, localPath.toFile());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            armazenamentoS3Service.upload(localPath, key);
        });

        assertTrue(ex.getMessage().contains("Erro ao subir para S3"));
    }
}