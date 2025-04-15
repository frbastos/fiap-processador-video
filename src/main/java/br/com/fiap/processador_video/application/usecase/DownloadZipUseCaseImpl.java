package br.com.fiap.processador_video.application.usecase;

import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.S3Object;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.application.gateway.ArmazenamentoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;
import br.com.fiap.processador_video.domain.usecase.DownloadZipUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DownloadZipUseCaseImpl implements DownloadZipUseCase {

    private final BuscarVideoPorIdUseCase buscarVideoPorIdUseCase;
    private final ArmazenamentoGateway armazenamentoGateway;

    @Override
    public InputStreamResource baixarZip(UUID videoId) throws VideoNotFoundException {
        Video video = buscarVideoPorIdUseCase.buscar(videoId);
        S3Object objeto = armazenamentoGateway.buscarObjeto(video.getZipPath());
        return new InputStreamResource(objeto.getObjectContent());
    }

}
