package br.com.fiap.processador_video.infra.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.fiap.processador_video.application.exception.VideoNotFoundException;
import br.com.fiap.processador_video.domain.entity.Video;
import br.com.fiap.processador_video.domain.usecase.DownloadZipUseCase;
import br.com.fiap.processador_video.domain.usecase.ListarVideosProcessadosUseCase;
import br.com.fiap.processador_video.domain.usecase.ProcessarVideoUseCase;
import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final ProcessarVideoUseCase processarVideoUseCase;
    private final ListarVideosProcessadosUseCase listarVideosProcessadosUseCase;
    private final DownloadZipUseCase downloadZipUseCase;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadVideos(@RequestParam MultipartFile[] files) {
        String usuarioId = UsuarioContext.getUsuarioId();
        Arrays.stream(files).parallel()
                .forEach(file -> processarVideoUseCase.executar(file, usuarioId));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("")
    public List<Video> listarVideosProcessados() {
        String usuarioId = UsuarioContext.getUsuarioId();
        return listarVideosProcessadosUseCase.listar(usuarioId);
    }

    @GetMapping("/{videoId}/download")
    public ResponseEntity<?> downloadZip(@PathVariable UUID videoId) {
        String usuarioId = UsuarioContext.getUsuarioId();
        try {
            InputStreamResource resource = downloadZipUseCase.baixarZip(videoId, usuarioId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"video.zip\"")
                    .body(resource);
        } catch (VideoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
}
