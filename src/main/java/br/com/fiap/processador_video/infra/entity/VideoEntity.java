package br.com.fiap.processador_video.infra.entity;

import java.util.UUID;

import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "video")
public class VideoEntity {

    @Id
    private UUID id;
    
    private String nomeOriginal;
    
    @Enumerated(EnumType.STRING)
    private VideoStatus status;

    private String zipPath;

    private String usuarioId;

}
