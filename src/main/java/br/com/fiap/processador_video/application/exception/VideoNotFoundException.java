package br.com.fiap.processador_video.application.exception;

import java.util.UUID;

public class VideoNotFoundException extends Exception {
    public VideoNotFoundException(UUID id) {
        super("Vídeo não encontrado com esse ID: " + id);
    }
}
