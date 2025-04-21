package br.com.fiap.processador_video.infra.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import br.com.fiap.processador_video.infra.entity.VideoEntity;
import br.com.fiap.processador_video.infra.gateway.mapper.VideoEntityMapper;
import br.com.fiap.processador_video.infra.repository.VideoRepositoryJpa;

class VideoGatewayImplTest {

    private VideoRepositoryJpa videoRepositoryJpa;
    private VideoEntityMapper mapper;
    private VideoGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        videoRepositoryJpa = mock(VideoRepositoryJpa.class);
        mapper = mock(VideoEntityMapper.class);
        gateway = new VideoGatewayImpl(videoRepositoryJpa, mapper);
    }

    @Test
    void deveSalvarVideoComSucesso() {
        String usuarioId = UUID.randomUUID().toString();


        // Arrange
        Video video = new Video(UUID.randomUUID(), "video.mp4", VideoStatus.PROCESSANDO, null, usuarioId);
        VideoEntity entity = new VideoEntity();
        when(mapper.toEntity(video)).thenReturn(entity);

        // Act
        gateway.salvar(video);

        // Assert
        verify(videoRepositoryJpa).saveAndFlush(entity);
    }

    @Test
    void deveListarTodosVideos() {
        String usuarioId = UUID.randomUUID().toString();

        // Arrange
        VideoEntity entity1 = new VideoEntity();
        VideoEntity entity2 = new VideoEntity();
        when(videoRepositoryJpa.findByUsuarioId(usuarioId)).thenReturn(List.of(entity1, entity2));

        Video video1 = new Video(UUID.randomUUID(), "video1", VideoStatus.CONCLUIDO, "zip1", usuarioId);
        Video video2 = new Video(UUID.randomUUID(), "video2", VideoStatus.ERRO, "zip2", usuarioId);

        when(mapper.toDomain(entity1)).thenReturn(video1);
        when(mapper.toDomain(entity2)).thenReturn(video2);

        // Act
        List<Video> videos = gateway.listarVideos(usuarioId);

        // Assert
        assertEquals(2, videos.size());
        assertTrue(videos.contains(video1));
        assertTrue(videos.contains(video2));
    }

    @Test
    void deveBuscarVideoPorIdComSucesso() {
        String usuarioId = UUID.randomUUID().toString();

        // Arrange
        UUID videoId = UUID.randomUUID();
        VideoEntity entity = new VideoEntity();
        Video video = new Video(videoId, "video.mp4", VideoStatus.CONCLUIDO, "path", usuarioId);

        when(videoRepositoryJpa.findById(videoId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(video);

        // Act
        Optional<Video> resultado = gateway.buscarPorId(videoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(video, resultado.get());
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrarVideo() {
        // Arrange
        UUID videoId = UUID.randomUUID();
        when(videoRepositoryJpa.findById(videoId)).thenReturn(Optional.empty());

        // Act
        Optional<Video> resultado = gateway.buscarPorId(videoId);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}

