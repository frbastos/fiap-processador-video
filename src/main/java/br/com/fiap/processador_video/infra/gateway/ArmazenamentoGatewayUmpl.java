package br.com.fiap.processador_video.infra.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

import br.com.fiap.processador_video.application.gateway.ArmazenamentoGateway;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArmazenamentoGatewayUmpl implements ArmazenamentoGateway {
    private final AmazonS3 s3;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Override
    public S3Object buscarObjeto(String s3Key) {
        return s3.getObject(bucket, s3Key);
    }

}
