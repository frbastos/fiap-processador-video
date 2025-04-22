package br.com.fiap.processador_video.domain.valuesobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import br.com.fiap.processador_video.infra.exception.UsuarioNaoEncontradoNoHeaderException;
import br.com.fiap.processador_video.infra.filter.UsuarioContextFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class UsuarioContextFilterTest {

    private final UsuarioContextFilter filter = new UsuarioContextFilter();

    @AfterEach
    void tearDown() {
        UsuarioContext.clear();
    }

    @Test
    void deveSetarUsuarioIdQuandoTokenValido() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String sub = "abc-123";
        String token = gerarTokenComSub(sub);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        doAnswer(invocation -> {
            assertEquals(sub, UsuarioContext.getUsuarioId());
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(UsuarioContext.getUsuarioId()); // deve limpar depois
    }

    @Test
    void deveIgnorarFiltroQuandoProtocoloNaoHttp11OuHttp2() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/3.0");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void deveLancarExcecaoQuandoAuthorizationAusente() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("Authorization")).thenReturn(null);

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain));

        assertEquals("Header Authorization não encontrado ou inválido.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoAuthorizationNaoComecaComBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("Authorization")).thenReturn("Token abc");

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain));

        assertEquals("Header Authorization não encontrado ou inválido.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSubAusenteNoToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String token = gerarTokenSemSub();

        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        UsuarioNaoEncontradoNoHeaderException ex = assertThrows(
                UsuarioNaoEncontradoNoHeaderException.class,
                () -> filter.doFilterInternal(request, response, chain));

        assertEquals("Claim 'sub' não encontrada no token.", ex.getMessage());
    }

    private String gerarTokenComSub(String sub) {
        return JWT.create()
                .withSubject(sub)
                .withIssuedAt(new Date())
                .sign(Algorithm.none());
    }

    private String gerarTokenSemSub() {
        return JWT.create()
                .withIssuedAt(new Date())
                .sign(Algorithm.none());
    }
}