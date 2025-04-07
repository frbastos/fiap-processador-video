package br.com.fiap.processador_video.infra.repository;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.fiap.processador_video.application.repository.VideoRepository;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import br.com.fiap.processador_video.infra.entity.VideoEntity;
import br.com.fiap.processador_video.infra.repository.mapper.VideoEntityMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepository {

    private final VideoRepositoryJpa videoRepositoryJpa;
    private final VideoEntityMapper mapper;

    @Override
    public void salvar(Video video) {
        VideoEntity entity = mapper.toEntity(video);
        videoRepositoryJpa.saveAndFlush(entity);
    }

    @Override
    public void atualizarStatus(UUID videoId, VideoStatus status) {
        videoRepositoryJpa.atualizarStatus(videoId, status);
    }

}
