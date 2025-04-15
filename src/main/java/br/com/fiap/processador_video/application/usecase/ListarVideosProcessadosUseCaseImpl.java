package br.com.fiap.processador_video.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.processador_video.application.gateway.VideoGateway;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.ListarVideosProcessadosUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarVideosProcessadosUseCaseImpl implements ListarVideosProcessadosUseCase {

    private final VideoGateway videoGateway;

    @Override
    public List<Video> listar() {
        return videoGateway.listarVideos();
    }

}
