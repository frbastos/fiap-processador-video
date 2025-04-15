package br.com.fiap.processador_video.domain.usecase;

import org.springframework.web.multipart.MultipartFile;

public interface ProcessarVideoUseCase {

    void executar(MultipartFile file);

}
