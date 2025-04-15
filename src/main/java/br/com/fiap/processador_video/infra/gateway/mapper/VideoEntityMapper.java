package br.com.fiap.processador_video.infra.gateway.mapper;

import org.springframework.stereotype.Component;

import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.infra.entity.VideoEntity;

@Component
public class VideoEntityMapper {

    public Video toDomain(VideoEntity entity){
        return new Video(entity.getId(), entity.getNomeOriginal(), entity.getStatus(), entity.getZipPath());
    }

    public VideoEntity toEntity(Video video){
        return new VideoEntity(video.getId(), video.getNomeOriginal(), video.getStatus(), video.getZipPath());
    }

}
