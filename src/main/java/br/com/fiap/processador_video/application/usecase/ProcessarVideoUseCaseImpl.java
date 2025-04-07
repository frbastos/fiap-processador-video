package br.com.fiap.processador_video.application.usecase;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.application.repository.VideoRepository;
import br.com.fiap.processador_video.application.service.ArquivoTemporarioService;
import br.com.fiap.processador_video.application.service.ExtratorFrameService;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.ProcessarVideoUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessarVideoUseCaseImpl implements ProcessarVideoUseCase {

    private final VideoRepository videoRepository;
    private final ExtratorFrameService extratorFrameService;
    private final ArquivoTemporarioService arquivoTemporarioService;

    @Value("${storage.temp-directory}")
    private String tempDirectory;

    // @Async
    @Override
    public void executar(MultipartFile file) {
        UUID videoId = UUID.randomUUID();
        try {
            Path videoPath = arquivoTemporarioService.salvarVideoTemporario(file, videoId.toString());

            Video video = new Video(videoId, file.getOriginalFilename(), VideoStatus.PROCESSANDO);
            videoRepository.salvar(video);

            Path outputDir = videoPath.getParent();
            extratorFrameService.extrairFrames(videoId.toString(), videoPath, outputDir);

            videoRepository.atualizarStatus(videoId, VideoStatus.CONCLUIDO);

            arquivoTemporarioService.deletarArquivo(videoPath);

        } catch (Exception e) {
            log.error("Erro ao processar vídeo " + videoId.toString(), e.getMessage());
            e.printStackTrace();
            videoRepository.atualizarStatus(videoId, VideoStatus.ERRO);
        }
    }

}
