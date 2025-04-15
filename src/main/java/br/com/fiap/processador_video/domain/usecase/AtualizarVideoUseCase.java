package br.com.fiap.processador_video.domain.usecase;

import java.util.UUID;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

public interface AtualizarVideoUseCase {

    void atualizar(UUID videoId, VideoStatus status) throws VideoNotFoundException;

    void atualizarZipPath(UUID videoId, String zipPath) throws VideoNotFoundException;

}
