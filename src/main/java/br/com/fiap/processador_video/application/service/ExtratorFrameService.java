package br.com.fiap.processador_video.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExtratorFrameService {

    public void extrairFrames(String videoId, Path videoPath, Path outputDir) throws IOException, InterruptedException {
        // salva 1 frame por segundo com nomes frame_0001.jpg, etc
        String outputPattern = outputDir.resolve("frame_%04d.jpg").toString();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath.toString(),
                "-vf", "fps=1", // extrai 1 frame por segundo
                outputPattern);

        // juntar stderr + stdout
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[FFmpeg][" + videoId + "] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Falha ao executar FFmpeg para o vídeo: " + videoPath.getFileName());
        }

    }

}
