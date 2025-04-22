package br.com.fiap.processador_video.infra.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.fiap.processador_video.domain.valueobjects.UsuarioContext;
import br.com.fiap.processador_video.infra.exception.UsuarioNaoEncontradoNoHeaderException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UsuarioContextFilter extends OncePerRequestFilter {

    private static final String USER_HEADER = "X-User-Sub";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            if (!"HTTP/1.1".equals(request.getProtocol()) && !"HTTP/2.0".equals(request.getProtocol())) {
                filterChain.doFilter(request, response);
                return;
            }

            String usuarioId = request.getHeader(USER_HEADER);

            if (usuarioId == null || usuarioId.isBlank()) {
                throw new UsuarioNaoEncontradoNoHeaderException("Cabeçalho obrigatório 'X-User-Sub' não encontrado.");
            }

            UsuarioContext.setUsuarioId(usuarioId);
            filterChain.doFilter(request, response);
        } finally {
            UsuarioContext.clear();
        }
    }

}
