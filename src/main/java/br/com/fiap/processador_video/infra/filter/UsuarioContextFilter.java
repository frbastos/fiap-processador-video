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

    private static final String AUTH_HEADER = "Authorization-IdToken";

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignora endpoints do actuator
        if (path != null && path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (!"HTTP/1.1".equals(request.getProtocol()) && !"HTTP/2.0".equals(request.getProtocol())) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = request.getHeader(AUTH_HEADER);
            if (token == null) {
                throw new UsuarioNaoEncontradoNoHeaderException("Header Authorization não encontrado ou inválido.");
            }

            // Extraindo Usuario ID
            DecodedJWT jwt = JWT.decode(token);
            String usuarioId = jwt.getSubject();
            String emailUsuario = jwt.getClaim("email").asString();

            if (usuarioId == null || usuarioId.isBlank()) {
                throw new UsuarioNaoEncontradoNoHeaderException("Claim 'sub' não encontrada no token.");
            }

            UsuarioContext.setUsuario(usuarioId, emailUsuario);
            filterChain.doFilter(request, response);
        } finally {
            UsuarioContext.clear();
        }
    }

}
