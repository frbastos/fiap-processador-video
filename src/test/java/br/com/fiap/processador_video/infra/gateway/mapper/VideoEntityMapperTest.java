package br.com.fiap.processador_video.infra.gateway.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import br.com.fiap.processador_video.infra.entity.VideoEntity;

class VideoEntityMapperTest {

    private VideoEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VideoEntityMapper();
    }

    @Test
    void deveConverterParaDomain() {
        UUID id = UUID.randomUUID();
        String usuarioId = UUID.randomUUID().toString();

        VideoEntity entity = new VideoEntity(id, "video.mp4", VideoStatus.CONCLUIDO, "caminho.zip", usuarioId);

        Video domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getNomeOriginal(), domain.getNomeOriginal());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getZipPath(), domain.getZipPath());
    }

    @Test
    void deveConverterParaEntity() {
        UUID id = UUID.randomUUID();
        String usuarioId = UUID.randomUUID().toString();

        Video domain = new Video(id, "video.mp4", VideoStatus.PROCESSANDO, "video.zip", usuarioId);

        VideoEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getNomeOriginal(), entity.getNomeOriginal());
        assertEquals(domain.getStatus(), entity.getStatus());
        assertEquals(domain.getZipPath(), entity.getZipPath());
    }
}

