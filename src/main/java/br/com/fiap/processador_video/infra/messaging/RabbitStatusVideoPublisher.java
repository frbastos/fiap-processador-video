package br.com.fiap.processador_video.infra.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.processador_video.application.gateway.PublicarStatusVideoGateway;
import br.com.fiap.processador_video.domain.event.StatusVideoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitStatusVideoPublisher implements PublicarStatusVideoGateway {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.video}")
    private String queue;

    @Override
    public void publicar(StatusVideoEvent event) throws JsonProcessingException, AmqpException{
        rabbitTemplate.convertAndSend(queue, toMessage(event));
    }

    private String toMessage(StatusVideoEvent event) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(event);
    }

}
