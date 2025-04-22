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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import br.com.fiap.processador_video.infra.exception.UsuarioNaoEncontradoNoHeaderException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class UsuarioContextFilterTest {

    private final UsuarioContextFilter filter = new UsuarioContextFilter();

    @AfterEach
    void limpar() {
        UsuarioContext.clear();
    }

    private String gerarTokenComSub(String sub) {
        return JWT.create()
                .withSubject(sub)
                .sign(Algorithm.HMAC256("segredo-teste"));
    }

    @Test
    void deveSetarUsuarioIdQuandoTokenValido() throws ServletException, IOException {
        String usuarioId = "usuario-123";
        String token = gerarTokenComSub(usuarioId);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("Authorization-IdToken")).thenReturn(token);

        doAnswer(invocation -> {
            assertEquals(usuarioId, UsuarioContext.getUsuarioId());
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void deveIgnorarSeProtocoloNaoForHttp1OuHttp2() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("FTP/1.0");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(UsuarioContext.getUsuarioId());
    }

    @Test
    void deveLancarExcecaoSeAuthorizationForNulo() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("Authorization-IdToken")).thenReturn(null);

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain)
        );

        assertEquals("Header Authorization não encontrado ou inválido.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeTokenSemSub() {
        String tokenSemSub = JWT.create().sign(Algorithm.HMAC256("segredo-teste"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/2.0");
        when(request.getHeader("Authorization-IdToken")).thenReturn(tokenSemSub);

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain)
        );

        assertEquals("Claim 'sub' não encontrada no token.", ex.getMessage());
    }
}
