package br.com.fiap.processador_video.infra.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.processador_video.application.gateway.PublicarFrameGateway;
import br.com.fiap.processador_video.domain.entity.FramePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitFramePublisher implements PublicarFrameGateway {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.frame}")
    private String queue;

    @Override
    public void publicar(FramePayload payload) throws JsonProcessingException, AmqpException {
        rabbitTemplate.convertAndSend(queue, toMessage(payload));
    }

    private String toMessage(FramePayload payload) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(payload);
    }

}
