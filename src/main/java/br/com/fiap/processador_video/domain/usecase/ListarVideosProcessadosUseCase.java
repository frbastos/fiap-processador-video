package br.com.fiap.processador_video.domain.usecase;

import java.util.List;

import br.com.fiap.processador_video.domain.entity.Video;

public interface ListarVideosProcessadosUseCase{

    List<Video> listar();

}
