package br.com.fiap.processador_video.infra.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import br.com.fiap.processador_video.infra.exception.UsuarioNaoEncontradoNoHeaderException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UsuarioContextFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            if (!"HTTP/1.1".equals(request.getProtocol()) && !"HTTP/2.0".equals(request.getProtocol())) {
                filterChain.doFilter(request, response);
                return;
            }

            String authorization = request.getHeader(AUTH_HEADER);
            if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
                throw new UsuarioNaoEncontradoNoHeaderException("Header Authorization não encontrado ou inválido.");
            }

            String token = authorization.substring(BEARER_PREFIX.length());
            DecodedJWT jwt = JWT.decode(token);
            String usuarioId = jwt.getSubject();

            if (usuarioId == null || usuarioId.isBlank()) {
                throw new UsuarioNaoEncontradoNoHeaderException("Claim 'sub' não encontrada no token.");
            }

            UsuarioContext.setUsuarioId(usuarioId);
            filterChain.doFilter(request, response);
        } finally {
            UsuarioContext.clear();
        }
    }

}
