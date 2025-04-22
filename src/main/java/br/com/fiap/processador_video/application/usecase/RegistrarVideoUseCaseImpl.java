package br.com.fiap.processador_video.application.usecase;

import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.RegistrarVideoUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrarVideoUseCaseImpl implements RegistrarVideoUseCase {

    private final VideoGateway videoRepository;

    @Transactional
    @Override
    public Video registrar(MultipartFile file, String usuarioId) {

        log.info("Registrando vídeo ...");

        Video video = null;
        try {
            UUID videoId = UUID.randomUUID();
            String nomeOriginal = file.getOriginalFilename();
    
            video = new Video(videoId, nomeOriginal, VideoStatus.PROCESSANDO, null, usuarioId);
            videoRepository.salvar(video);
        } catch (AmqpException e) {
            throw new RuntimeException("Erro ao publicar evento do vídeo", e);
        }

        return video;
    }

}
