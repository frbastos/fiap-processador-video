package br.com.fiap.processador_video.application.service;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ArquivoTemporarioServiceTest {

    private ArquivoTemporarioService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new ArquivoTemporarioService();

        // Injeta o valor do baseTempDirectory via reflection
        var field = ArquivoTemporarioService.class.getDeclaredField("baseTempDirectory");
        field.setAccessible(true);
        field.set(service, "videos-temp-test");
    }

    @Test
    void deveCriarDiretorioTemporario() throws IOException {
        String videoId = "12345";
        Path path = service.criarDiretorioTemporario(videoId);

        assertTrue(Files.exists(path));
        assertTrue(Files.isDirectory(path));

        // Cleanup
        service.deletarDiretorioTemporario(path);
    }

    @Test
    void deveSalvarVideoTemporario() throws IOException {
        String videoId = "video-test";
        byte[] content = "conteudo do video".getBytes();
        MultipartFile file = new MockMultipartFile("file", "teste.mp4", "video/mp4", content);

        Path videoPath = service.salvarVideoTemporario(file, videoId);

        assertTrue(Files.exists(videoPath));
        assertTrue(videoPath.getFileName().toString().endsWith(".mp4"));

        // Cleanup
        service.deletarDiretorioTemporario(videoPath.getParent());
    }

    @Test
    void deveDeletarArquivo() throws IOException {
        Path tempFile = Files.createTempFile("teste-delete", ".tmp");

        assertTrue(Files.exists(tempFile));

        service.deletarArquivo(tempFile);

        assertFalse(Files.exists(tempFile));
    }

    @Test
    void deveDeletarDiretorioComArquivos() throws IOException {
        Path dir = Files.createTempDirectory("temp-dir-test");
        Files.createFile(dir.resolve("arquivo1.txt"));
        Files.createFile(dir.resolve("arquivo2.txt"));

        assertTrue(Files.exists(dir));

        service.deletarDiretorioTemporario(dir);

        assertFalse(Files.exists(dir));
    }
}

