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

@Service
@RequiredArgsConstructor
public class RegistrarVideoUseCaseImpl implements RegistrarVideoUseCase {

    private final VideoGateway videoRepository;

    @Transactional
    @Override
    public Video registrar(MultipartFile file) {

        Video video = null;
        try {
            UUID videoId = UUID.randomUUID();
            String nomeOriginal = file.getOriginalFilename();
    
            video = new Video(videoId, nomeOriginal, VideoStatus.PROCESSANDO, null);
            videoRepository.salvar(video);
        } catch (AmqpException e) {
            throw new RuntimeException("Erro ao publicar evento do vídeo", e);
        }

        return video;
    }

}
