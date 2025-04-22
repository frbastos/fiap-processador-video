package br.com.fiap.processador_video.application.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarErroProcessamento(String email, String videoId, String motivoErro) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(email);
            mensagem.setSubject("Erro no processamento do vídeo");
            mensagem.setText("O vídeo com ID " + videoId + " não pôde ser processado.\nMotivo: " + motivoErro);
            mailSender.send(mensagem);

            log.info("Email de erro enviado para {}", email);
        } catch (Exception ex) {
            log.error("Erro ao enviar e-mail: {}", ex.getMessage(), ex);
        }
    }
}