package br.com.fiap.processador_video.application.gateway;

import com.amazonaws.services.s3.model.S3Object;

public interface ArmazenamentoGateway {

    S3Object buscarObjeto(String s3Key);

}
