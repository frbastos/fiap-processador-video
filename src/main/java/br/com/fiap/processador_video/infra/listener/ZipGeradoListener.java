package br.com.fiap.processador_video.infra.listener;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.event.ZipGeradoEvent;
import br.com.fiap.processador_video.domain.usecase.AtualizarVideoUseCase;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipGeradoListener {

    private final BuscarVideoPorIdUseCase buscarVideoPorIdUseCase;
    private final AtualizarVideoUseCase atualizarVideoUseCase;

    @RabbitListener(queues = "${queue.zip}")
    public void receberStatusVideo(String evento) {
        ZipGeradoEvent zipGeradoEvent = toObject(evento);
        UUID videoId = UUID.fromString(zipGeradoEvent.getVideoId());
        try {
            Video video = buscarVideoPorIdUseCase.buscar(videoId);
            if(!video.getStatus().equals(VideoStatus.CONCLUIDO) && video.getZipPath() != null) return;
            atualizarVideoUseCase.atualizarZipPath(videoId, zipGeradoEvent.getPath());
        } catch (VideoNotFoundException e) {
            log.info("Vídeo não encontrado com UUID: " + videoId);
        }
    }

    private ZipGeradoEvent toObject(String evento) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(evento, ZipGeradoEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao receber mensagem zip gerado", e);
        }
    }

}
