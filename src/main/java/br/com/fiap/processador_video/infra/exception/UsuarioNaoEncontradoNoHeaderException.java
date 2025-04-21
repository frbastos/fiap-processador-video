package br.com.fiap.processador_video.infra.exception;

public class UsuarioNaoEncontradoNoHeaderException extends RuntimeException {

    public UsuarioNaoEncontradoNoHeaderException(String mensagem) {
        super(mensagem);
    }

}
