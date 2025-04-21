package br.com.fiap.processador_video.infra.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import br.com.fiap.processador_video.infra.exception.UsuarioNaoEncontradoNoHeaderException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class UsuarioContextFilterTest {

    private final UsuarioContextFilter filter = new UsuarioContextFilter();

    @AfterEach
    void clearContext() {
        UsuarioContext.clear();
    }

    @Test
    void deveSetarUsuarioIdQuandoHeaderPresente() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String usuarioId = "abc-123";
        when(request.getHeader("X-User-Sub")).thenReturn(usuarioId);

        // Captura o valor de UsuarioContext dentro do fluxo do filtro
        doAnswer(invocation -> {
            assertEquals(usuarioId, UsuarioContext.getUsuarioId()); // dentro da execução
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response); // garante que foi chamado
    }

    @Test
    void deveLancarExcecaoQuandoHeaderNaoPresente() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-User-Sub")).thenReturn(null);

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain));

        assertEquals("Cabeçalho obrigatório 'X-User-Sub' não encontrado.", ex.getMessage());
    }

    @Test
    void deveLimparUsuarioContextMesmoComErro() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("X-User-Sub")).thenReturn(null);

        try {
            filter.doFilterInternal(request, response, chain);
        } catch (UsuarioNaoEncontradoNoHeaderException ignored) {
        }

        assertNull(UsuarioContext.getUsuarioId());
    }
}
