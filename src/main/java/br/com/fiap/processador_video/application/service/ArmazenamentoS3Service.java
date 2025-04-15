package br.com.fiap.processador_video.application.service;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArmazenamentoS3Service {

    private final AmazonS3 amazonS3;

    @Value("${storage.s3.bucket}")
    private String bucket;

    public String upload(Path localPath, String key) {
        try {
            amazonS3.putObject(bucket, key, localPath.toFile());
            return key;
        } catch (AmazonServiceException e) {
            throw new RuntimeException("Erro ao subir para S3: " + key, e);
        }
    }
}