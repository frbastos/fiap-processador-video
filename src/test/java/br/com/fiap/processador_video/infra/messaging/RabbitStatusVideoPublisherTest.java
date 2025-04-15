package br.com.fiap.processador_video.infra.messaging;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import br.com.fiap.processador_video.domain.event.StatusVideoEvent;
import br.com.fiap.processador_video.domain.valueobjects.VideoStatus;

class RabbitStatusVideoPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private RabbitStatusVideoPublisher publisher;

    @BeforeEach
    void setUp() throws Exception {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new RabbitStatusVideoPublisher(rabbitTemplate);

        // Injeta o valor do @Value
        var field = RabbitStatusVideoPublisher.class.getDeclaredField("queue");
        field.setAccessible(true);
        field.set(publisher, "queue.video.test");
    }

    @Test
    void devePublicarMensagemNoRabbit() throws JsonProcessingException {
        // Arrange
        StatusVideoEvent event = new StatusVideoEvent(UUID.randomUUID(), VideoStatus.CONCLUIDO);

        // Act
        publisher.publicar(event);

        // Assert
        verify(rabbitTemplate).convertAndSend(eq("queue.video.test"), anyString());
    }

    @Test
    void deveLancarExcecaoQuandoJsonForInvalido() {
        // Arrange
        StatusVideoEvent event = mock(StatusVideoEvent.class);
        try {
            when(event.getVideoId()).thenThrow(new RuntimeException("erro de serialização"));

            // Act & Assert
            assertThrows(JsonProcessingException.class, () -> publisher.publicar(event));
        } catch (AmqpException e) {
            fail("Não deveria lançar AmqpException nesse teste");
        }
    }
}
