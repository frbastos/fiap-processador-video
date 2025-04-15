package br.com.fiap.processador_video.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.BuscarVideoPorIdUseCase;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BuscarVideoPorIdUseCaseImpl implements BuscarVideoPorIdUseCase{

    private final VideoGateway videoGateway;

    @Override
    public Video buscar(UUID uuid) throws VideoNotFoundException{
        return videoGateway.buscarPorId(uuid).orElseThrow(() -> new VideoNotFoundException(uuid));
    }

}
