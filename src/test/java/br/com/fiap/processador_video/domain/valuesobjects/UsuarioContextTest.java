package br.com.fiap.processador_video.domain.valuesobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;

class UsuarioContextTest {

    @AfterEach
    void tearDown() {
        UsuarioContext.clear();
    }

    @Test
    void deveSetarEObterUsuarioIdCorretamente() {
        String id = "abc-123";

        UsuarioContext.setUsuarioId(id);

        assertEquals(id, UsuarioContext.getUsuarioId());
    }

    @Test
    void deveLimparUsuarioId() {
        UsuarioContext.setUsuarioId("abc-123");
        UsuarioContext.clear();

        assertNull(UsuarioContext.getUsuarioId());
    }
}

