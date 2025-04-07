package br.com.fiap.processador_video.application.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArquivoTemporarioService {

    @Value("${storage.temp-directory:videos-temp}")
    private String baseTempDirectory;

    public Path criarDiretorioTemporario(String videoId) throws IOException {
        String projectRoot = System.getProperty("user.dir");
        Path tempDir = Paths.get(projectRoot, "videos-temp", videoId);
        Files.createDirectories(tempDir);
        return tempDir;
    }

    public Path salvarVideoTemporario(MultipartFile file, String videoId) throws IOException {
        Path tempDir = criarDiretorioTemporario(videoId);
        Files.createDirectories(tempDir);

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = "video_" + videoId + extension;
        Path videoPath = tempDir.resolve(fileName);

        file.transferTo(videoPath.toFile());

        return videoPath;
    }

    public void deletarArquivo(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Erro ao deletar arquivo temporário: " + path);
        }
    }

    public void deletarDiretorioTemporario(Path dir) {
        try (Stream<Path> files = Files.walk(dir)) {
            files.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.err.println("Erro ao limpar diretório temporário: " + dir);
        }
    }

}
