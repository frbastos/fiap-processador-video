package br.com.fiap.processador_video.infra.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import br.com.fiap.processador_video.infra.entity.VideoEntity;
import jakarta.transaction.Transactional;

@Repository
public interface VideoRepositoryJpa extends JpaRepository<VideoEntity, UUID>{

    @Modifying
    @Transactional
    @Query("UPDATE VideoEntity v SET v.status = :status WHERE v.id = :videoId")
    void atualizarStatus(@Param("videoId") UUID videoId, @Param("status") VideoStatus status);

    List<VideoEntity> findByUsuarioId(String usuarioId);

}
