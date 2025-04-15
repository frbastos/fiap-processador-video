package br.com.fiap.processador_video.application.usecase;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.application.service.ArquivoTemporarioService;
import br.com.fiap.processador_video.application.service.ExtratorFrameService;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.AtualizarVideoUseCase;
import br.com.fiap.processador_video.domain.usecase.ProcessarVideoUseCase;
import br.com.fiap.processador_video.domain.usecase.RegistrarVideoUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessarVideoUseCaseImpl implements ProcessarVideoUseCase {

    private final ExtratorFrameService extratorFrameService;
    private final ArquivoTemporarioService arquivoTemporarioService;
    private final RegistrarVideoUseCase registrarVideoUseCase;
    private final AtualizarVideoUseCase atualizarVideoUseCase;

    @Value("${storage.temp-directory}")
    private String tempDirectory;

    @Async
    @Override
    public void executar(MultipartFile file) {
        Video video = null;
        try {
            video = registrarVideoUseCase.registrar(file);

            Path videoPath = arquivoTemporarioService.salvarVideoTemporario(file, video.getId().toString());
            Path outputDir = videoPath.getParent();

            int totalFramesEsperados = extratorFrameService.contarFramesEsperadosPorDuracao(videoPath);

            int framesProcessados = extratorFrameService.extrairFrames(video.getId().toString(), videoPath, outputDir);

            double percentual = ((double) framesProcessados / totalFramesEsperados) * 100;

            if (percentual >= 90) {
                atualizarVideoUseCase.atualizar(video.getId(), VideoStatus.CONCLUIDO);
            } else {
                log.warn("Apenas {:.2f}% dos frames foram processados", percentual);
                atualizarVideoUseCase.atualizar(video.getId(), VideoStatus.ERRO);
            }

            arquivoTemporarioService.deletarArquivo(videoPath);

        } catch (Exception e) {
            e.printStackTrace();
            if (video != null) {
                log.error("Erro ao processar vídeo " + video.getId(), e.getMessage());
                try {
                    atualizarVideoUseCase.atualizar(video.getId(), VideoStatus.ERRO);
                } catch (VideoNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
