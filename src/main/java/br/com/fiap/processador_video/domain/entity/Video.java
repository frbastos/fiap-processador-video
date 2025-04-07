package br.com.fiap.processador_video.domain.entity;

import java.util.UUID;

import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Video {

    private UUID id;
    private String nomeOriginal;
    private VideoStatus status;

    public void processando(){
        this.status = VideoStatus.PROCESSANDO;
    }

    public void erro(){
        this.status = VideoStatus.ERRO;
    }

    public void concluido(){
        this.status = VideoStatus.CONCLUIDO;
    }

}
