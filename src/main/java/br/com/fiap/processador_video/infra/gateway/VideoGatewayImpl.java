package br.com.fiap.processador_video.infra.gateway;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.infra.entity.VideoEntity;
import br.com.fiap.processador_video.infra.gateway.mapper.VideoEntityMapper;
import br.com.fiap.processador_video.infra.repository.VideoRepositoryJpa;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VideoGatewayImpl implements VideoGateway {

    private final VideoRepositoryJpa videoRepositoryJpa;
    private final VideoEntityMapper mapper;

    @Override
    public void salvar(Video video) {
        VideoEntity entity = mapper.toEntity(video);
        videoRepositoryJpa.saveAndFlush(entity);
    }

    @Override
    public List<Video> listarVideos() {
        return videoRepositoryJpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Video> buscarPorId(UUID uuid) {
        return videoRepositoryJpa.findById(uuid).map(mapper::toDomain);
    }

}
