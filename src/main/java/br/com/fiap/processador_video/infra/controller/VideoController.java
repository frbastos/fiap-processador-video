package br.com.fiap.processador_video.infra.controller;

import java.util.Arrays;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.domain.usecase.ProcessarVideoUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final ProcessarVideoUseCase processarVideoUseCase;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideos(@RequestParam MultipartFile[] files) {
        Arrays.stream(files).parallel()
                .forEach(processarVideoUseCase::executar);
        return ResponseEntity.accepted().build();
    }
}
