package br.com.fiap.processador_video.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class VideoTest {

    @Test
    void deveInstanciarComConstrutorCompleto() {
        UUID id = UUID.randomUUID();
        Video video = new Video(id, "video.mp4", VideoStatus.PROCESSANDO, "path/zip");

        assertEquals(id, video.getId());
        assertEquals("video.mp4", video.getNomeOriginal());
        assertEquals(VideoStatus.PROCESSANDO, video.getStatus());
        assertEquals("path/zip", video.getZipPath());
    }

    @Test
    void deveAlterarStatusParaConcluido() {
        Video video = new Video();
        video.concluido();
        assertEquals(VideoStatus.CONCLUIDO, video.getStatus());
    }

    @Test
    void deveAlterarStatusParaErro() {
        Video video = new Video();
        video.erro();
        assertEquals(VideoStatus.ERRO, video.getStatus());
    }

    @Test
    void deveAlterarStatusParaProcessando() {
        Video video = new Video();
        video.processando();
        assertEquals(VideoStatus.PROCESSANDO, video.getStatus());
    }

    @Test
    void deveAlterarZipPath() {
        Video video = new Video();
        video.setZipPath("videos/123.zip");
        assertEquals("videos/123.zip", video.getZipPath());
    }

    @Test
    void deveAlterarId() {
        UUID novoId = UUID.randomUUID();
        Video video = new Video();
        video.setId(novoId);
        assertEquals(novoId, video.getId());
    }

    @Test
    void equalsEHashCodeDevemFuncionar() {
        UUID id = UUID.randomUUID();

        Video v1 = new Video(id, "video.mp4", VideoStatus.PROCESSANDO, "a.zip");
        Video v2 = new Video(id, "video.mp4", VideoStatus.PROCESSANDO, "a.zip");

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void videosComIdsDiferentesDevemSerDiferentes() {
        Video v1 = new Video(UUID.randomUUID(), "video1", VideoStatus.ERRO, "1.zip");
        Video v2 = new Video(UUID.randomUUID(), "video2", VideoStatus.CONCLUIDO, "2.zip");

        assertNotEquals(v1, v2);
    }
}
