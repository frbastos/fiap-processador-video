package br.com.fiap.processador_video.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtratorFrameService {

    private final ArmazenamentoS3Service armazenamentoS3Service;

    @Transactional
    public int extrairFrames(String videoId, Path videoPath, Path outputDir) throws IOException, InterruptedException {
        Process ffmpegProcess = iniciarFFmpeg(videoPath, outputDir);
        lerLogsFFmpegAsync(ffmpegProcess, videoId);

        AtomicInteger contador = new AtomicInteger(0);
        monitorarPublicacaoDeFrames(videoId, outputDir, ffmpegProcess, contador);

        verificarExitCode(ffmpegProcess, videoPath);
        return contador.get();
    }

    protected Process iniciarFFmpeg(Path videoPath, Path outputDir) throws IOException {
        String outputPattern = outputDir.resolve("frame_%04d.jpg").toString();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath.toString(),
                "-vf", "fps=1",
                outputPattern);
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    protected void lerLogsFFmpegAsync(Process process, String videoId) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // log.info("[FFmpeg][{}] {}", videoId, line);
                }
            } catch (IOException e) {
                log.error("Erro ao ler output do FFmpeg", e);
            }
        }).start();
    }

    protected void monitorarPublicacaoDeFrames(String videoId, Path outputDir, Process process, AtomicInteger contador)
            throws InterruptedException {
        Set<String> publicados = new HashSet<>();

        while (process.isAlive()) {
            publicarNovosFrames(videoId, outputDir, publicados, contador);
            Thread.sleep(500); // meio segundo
        }

        // Garantir que nenhum frame ficou pra trás
        publicarNovosFrames(videoId, outputDir, publicados, contador);
    }

    protected void publicarNovosFrames(String videoId, Path outputDir, Set<String> publicados, AtomicInteger contador) {
        try (Stream<Path> files = Files.list(outputDir)) {
            files.filter(path -> path.getFileName().toString().startsWith("frame_"))
                    .filter(path -> !publicados.contains(path.getFileName().toString()))
                    .forEach(path -> publicarFrame(videoId, path, publicados, contador));
        } catch (IOException e) {
            log.error("Erro ao acessar arquivos de frame", e);
        }
    }

    protected void publicarFrame(String videoId, Path path, Set<String> publicados, AtomicInteger contador) {
        String frameName = path.getFileName().toString();
        String s3Key = videoId + "/frames/" + frameName;

        try {
            // Upload do frame pro S3
            armazenamentoS3Service.upload(path, s3Key);

            // Remove arquivo local
            Files.deleteIfExists(path);

            publicados.add(frameName);
            contador.incrementAndGet();
        } catch (Exception e) {
            log.error("Erro ao publicar frame: {}", frameName, e);
        }
    }

    protected void verificarExitCode(Process process, Path videoPath) throws InterruptedException, IOException {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg falhou com código: " + exitCode + " ao processar " + videoPath);
        }
    }

    public int contarFramesEsperadosPorDuracao(Path videoPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    videoPath.toString()
            );
    
            Process process = builder.start();
    
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null) {
                    double duracaoSegundos = Double.parseDouble(line.trim());
                    return (int) Math.ceil(duracaoSegundos); // 10 frame por segundo
                }
            }
    
            int exitCode = process.waitFor();
            if (exitCode != 0) throw new IOException("ffprobe falhou com código: " + exitCode);
        } catch (Exception e) {
            log.error("Erro ao contar frames por duração com ffprobe", e);
        }
        return 0;
    }

}
