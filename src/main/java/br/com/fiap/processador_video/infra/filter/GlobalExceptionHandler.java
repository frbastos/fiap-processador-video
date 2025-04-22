package br.com.fiap.processador_video.infra.filter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.fiap.processador_video.infra.exception.UsuarioNaoEncontradoNoHeaderException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoNoHeaderException.class)
    public ResponseEntity<String> handleUsuarioNaoEncontradoNoHeaderException(
            UsuarioNaoEncontradoNoHeaderException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}