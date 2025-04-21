package br.com.fiap.processador_video.domain.valueobjects;

public class UsuarioContext {

    private static final ThreadLocal<String> usuarioId = new ThreadLocal<>();

    public static void setUsuarioId(String id) {
        usuarioId.set(id);
    }

    public static String getUsuarioId() {
        return usuarioId.get();
    }

    public static void clear() {
        usuarioId.remove();
    }
}
