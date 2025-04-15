package br.com.fiap.processador_video.domain.usecase;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;

public interface DownloadZipUseCase {

    InputStreamResource baixarZip(UUID videoId) throws VideoNotFoundException;

}
