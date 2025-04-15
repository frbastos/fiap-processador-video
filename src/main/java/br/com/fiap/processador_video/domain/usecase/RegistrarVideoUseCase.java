package br.com.fiap.processador_video.domain.usecase;

import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.domain.entity.Video;

public interface RegistrarVideoUseCase {

    Video registrar(MultipartFile file);

}
