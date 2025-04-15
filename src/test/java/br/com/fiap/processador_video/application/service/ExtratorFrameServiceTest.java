package br.com.fiap.processador_video.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtratorFrameServiceTest {

    private ArmazenamentoS3Service armazenamentoS3Service;
    private ExtratorFrameService extratorFrameService;

    @BeforeEach
    void setUp() {
        armazenamentoS3Service = mock(ArmazenamentoS3Service.class);
        extratorFrameService = spy(new ExtratorFrameService(armazenamentoS3Service));
    }

    @Test
    void deveExtrairFramesComSucesso() throws Exception {
        // Arrange
        Path videoPath = Files.createTempFile("video", ".mp4");
        Path outputDir = Files.createTempDirectory("frames");

        // Mocks
        Process fakeProcess = mock(Process.class);
        when(fakeProcess.isAlive()).thenReturn(false); // para pular o while
        when(fakeProcess.waitFor()).thenReturn(0); // para não lançar exceção

        doReturn(fakeProcess).when(extratorFrameService).iniciarFFmpeg(videoPath, outputDir);
        doNothing().when(extratorFrameService).lerLogsFFmpegAsync(any(), anyString());
        doNothing().when(extratorFrameService).publicarNovosFrames(anyString(), eq(outputDir), anySet(), any());
        doNothing().when(extratorFrameService).verificarExitCode(fakeProcess, videoPath);

        // Act
        int frames = extratorFrameService.extrairFrames("video123", videoPath, outputDir);

        // Assert
        assertEquals(0, frames); // contador inicia em 0, publicarNovosFrames está mocked
        verify(extratorFrameService).iniciarFFmpeg(videoPath, outputDir);
        verify(extratorFrameService).monitorarPublicacaoDeFrames(eq("video123"), eq(outputDir), eq(fakeProcess),
                any(AtomicInteger.class));
        verify(extratorFrameService).verificarExitCode(fakeProcess, videoPath);

        // Cleanup
        Files.deleteIfExists(videoPath);
        Files.deleteIfExists(outputDir);
    }

    @Test
    void devePublicarFrameComSucesso() throws Exception {
        // Arrange
        Path tempDir = Files.createTempDirectory("frame-test");
        Path framePath = tempDir.resolve("frame_0001.jpg");
        Files.writeString(framePath, "fake-content");

        AtomicInteger contador = new AtomicInteger(0);
        Set<String> publicados = new HashSet<>();

        // Act
        extratorFrameService.publicarFrame("video123", framePath, publicados, contador);

        // Assert
        verify(armazenamentoS3Service).upload(eq(framePath), eq("video123/frames/frame_0001.jpg"));
        assertTrue(publicados.contains("frame_0001.jpg"));
        assertEquals(1, contador.get());
        assertFalse(Files.exists(framePath));
    }

    @Test
    void devePublicarNovosFramesComSucesso() throws Exception {
        // Arrange
        Path tempDir = Files.createTempDirectory("frames-batch");
        Path frame2 = Files.writeString(tempDir.resolve("frame_0002.jpg"), "frame2");

        Set<String> publicados = new HashSet<>(List.of("frame_0001.jpg")); // simula frame1 já enviado
        AtomicInteger contador = new AtomicInteger(1);

        // Act
        extratorFrameService.publicarNovosFrames("vid123", tempDir, publicados, contador);

        // Assert
        verify(armazenamentoS3Service).upload(eq(frame2), eq("vid123/frames/frame_0002.jpg"));
        assertTrue(publicados.contains("frame_0002.jpg"));
        assertEquals(2, contador.get());
    }

    @Test
    void deveIgnorarFrameDuplicado() throws Exception {
        Path tempDir = Files.createTempDirectory("frames-batch");

        Set<String> publicados = new HashSet<>(Set.of("frame_0001.jpg"));
        AtomicInteger contador = new AtomicInteger(0);

        extratorFrameService.publicarNovosFrames("vid999", tempDir, publicados, contador);

        verifyNoInteractions(armazenamentoS3Service);
        assertEquals(0, contador.get());
    }

    @Test
    void deveTratarErroAoPublicarFrame() throws Exception {
        // Arrange
        Path tempDir = Files.createTempDirectory("frame-error");
        Path framePath = Files.writeString(tempDir.resolve("frame_9999.jpg"), "conteudo");

        doThrow(new RuntimeException("Erro de rede"))
                .when(armazenamentoS3Service).upload(any(), any());

        AtomicInteger contador = new AtomicInteger(0);
        Set<String> publicados = new HashSet<>();

        // Act
        extratorFrameService.publicarFrame("falha", framePath, publicados, contador);

        // Assert
        assertEquals(0, contador.get()); // não incrementa
        assertFalse(publicados.contains("frame_9999.jpg"));
        // frame ainda existe
        assertTrue(Files.exists(framePath));
    }

    @Test
    void deveRetornarZeroSeFfprobeFalhar() {
        // Arrange
        Path fake = Paths.get("arquivo_que_nao_existe.mp4");

        // Act
        int qtd = extratorFrameService.contarFramesEsperadosPorDuracao(fake);

        // Assert
        assertEquals(0, qtd);
    }

    @Test
    void deveLancarExcecaoSeExitCodeNaoForZero() throws Exception {
        Process process = mock(Process.class);
        when(process.waitFor()).thenReturn(1);

        IOException ex = assertThrows(IOException.class,
                () -> extratorFrameService.verificarExitCode(process, Paths.get("video.mp4")));

        assertTrue(ex.getMessage().contains("FFmpeg falhou com código"));
    }
}
