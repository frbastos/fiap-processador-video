package br.com.fiap.processador_video.application.usecase;

import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.application.gateway.PublicarStatusVideoGateway;
import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.event.StatusVideoEvent;
import br.com.fiap.processador_video.domain.usecase.AtualizarVideoUseCase;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AtualizarVideoUseCaseImpl implements AtualizarVideoUseCase {

    private final VideoGateway videoGateway;
    private final PublicarStatusVideoGateway publicarStatusVideoGateway;
    private final BuscarVideoPorIdUseCase buscarVideoPorIdUseCase;

    @Transactional
    @Override
    public void atualizar(UUID videoId, VideoStatus status) throws VideoNotFoundException{
        try {
            Video video = buscarVideoPorIdUseCase.buscar(videoId);
            video.concluido();
            videoGateway.salvar(video);
            publicarStatusVideoGateway.publicar(new StatusVideoEvent(videoId, VideoStatus.CONCLUIDO));
        } catch (JsonProcessingException | AmqpException e) {
            throw new RuntimeException("Erro ao publicar evento do vídeo", e);
        }
    }

    @Override
    public void atualizarZipPath(UUID videoId, String zipPath) throws VideoNotFoundException {
        Video video = buscarVideoPorIdUseCase.buscar(videoId);
        video.setZipPath(zipPath);
        videoGateway.salvar(video);
    }

}
