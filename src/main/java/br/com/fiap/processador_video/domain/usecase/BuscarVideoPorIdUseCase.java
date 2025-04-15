package br.com.fiap.processador_video.domain.usecase;

import java.util.UUID;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.domain.entity.Video;

public interface BuscarVideoPorIdUseCase {

    Video buscar(UUID uuid) throws VideoNotFoundException;

}
