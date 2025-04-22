package br.com.fiap.processador_video.domain.valueobjects;

public class UsuarioContext {

    private static final ThreadLocal<String> usuarioId = new ThreadLocal<>();
    private static final ThreadLocal<String> email = new ThreadLocal<>();

    public static void setUsuario(String id, String emailValue) {
        usuarioId.set(id);
        email.set(emailValue);
    }

    public static String getUsuarioId() {
        return usuarioId.get();
    }

    public static String getEmail() {
        return email.get();
    }

    public static void clear() {
        usuarioId.remove();
        email.remove();
    }

}
