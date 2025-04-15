package br.com.fiap.processador_video.application.gateway;

import org.springframework.amqp.AmqpException;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.fiap.processador_video.domain.event.StatusVideoEvent;

public interface PublicarStatusVideoGateway {
    
    void publicar(StatusVideoEvent event) throws JsonProcessingException, AmqpException;

}
