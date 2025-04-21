package br.com.fiap.processador_video.application.gateway;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.fiap.processador_video.domain.entity.Video;

public interface VideoGateway {

    void salvar(Video video);

    List<Video> listarVideos(String usuarioId);

    Optional<Video> buscarPorId(UUID uuid);

}
