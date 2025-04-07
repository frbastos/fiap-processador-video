package br.com.fiap.processador_video.application.repository;

import java.util.UUID;

import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

public interface VideoRepository {

    void salvar(Video video);

    void atualizarStatus(UUID videoId, VideoStatus status);

}
