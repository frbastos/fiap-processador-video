package br.com.fiap.processador_video.domain.entity;

import java.util.UUID;

import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Video {

    private UUID id;
    private String nomeOriginal;
    private VideoStatus status;
    private String zipPath;
    private String usuarioId;

    public void processando(){
        this.status = VideoStatus.PROCESSANDO;
    }

    public void erro(){
        this.status = VideoStatus.ERRO;
    }

    public void concluido(){
        this.status = VideoStatus.CONCLUIDO;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
