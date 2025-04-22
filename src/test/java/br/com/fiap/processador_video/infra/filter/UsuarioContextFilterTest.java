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
    void limparContexto() {
        UsuarioContext.clear();
    }

    @Test
    void deveSetarUsuarioIdQuandoProtocoloHttp1EHeaderPresente() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("X-User-Sub")).thenReturn("user-123");

        doAnswer(invocation -> {
            assertEquals("user-123", UsuarioContext.getUsuarioId());
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void deveSetarUsuarioIdQuandoProtocoloHttp2EHeaderPresente() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/2.0");
        when(request.getHeader("X-User-Sub")).thenReturn("user-456");

        doAnswer(invocation -> {
            assertEquals("user-456", UsuarioContext.getUsuarioId());
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void deveIgnorarFiltroQuandoProtocoloNaoEhHttp11OuHttp2() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTPS/1.1");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(UsuarioContext.getUsuarioId()); // não foi setado
    }

    @Test
    void deveLancarExcecaoQuandoProtocoloEhHttp11EMasHeaderNaoPresente() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("X-User-Sub")).thenReturn(null);

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain)
        );

        assertEquals("Cabeçalho obrigatório 'X-User-Sub' não encontrado.", ex.getMessage());
        assertNull(UsuarioContext.getUsuarioId());
    }
}
