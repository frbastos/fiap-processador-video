package br.com.fiap.processador_video.infra.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

class ArmazenamentoGatewayUmplTest {

    private AmazonS3 s3;
    private ArmazenamentoGatewayUmpl gateway;

    @BeforeEach
    void setUp() throws Exception {
        s3 = mock(AmazonS3.class);
        gateway = new ArmazenamentoGatewayUmpl(s3);

        // Injeta o bucket via reflection
        var field = ArmazenamentoGatewayUmpl.class.getDeclaredField("bucket");
        field.setAccessible(true);
        field.set(gateway, "bucket-test");
    }

    @Test
    void deveBuscarObjetoNoS3ComSucesso() {
        // Arrange
        String s3Key = "videos/123.zip";
        S3Object s3ObjectMock = mock(S3Object.class);
        when(s3.getObject("bucket-test", s3Key)).thenReturn(s3ObjectMock);

        // Act
        S3Object resultado = gateway.buscarObjeto(s3Key);

        // Assert
        assertNotNull(resultado);
        verify(s3).getObject("bucket-test", s3Key);
    }
}
