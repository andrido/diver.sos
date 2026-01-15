package com.ufc.diversos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String remetente;

    @Value("${app.api.url}")
    private String apiUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarEmailConfirmacao(String emailDestino, String token) {
        logger.info("📧 Iniciando envio de e-mail local para: {}", emailDestino);

        try {
            String link = apiUrl + "/auth/confirmar?token=" + token;
            String texto = """
                Olá!
                
                Seja bem-vindo à Diver.SOS.
                Para ativar sua conta, clique no link abaixo:
                
                %s
                
                Se você não solicitou, ignore este e-mail.
                """.formatted(link);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(emailDestino);
            message.setSubject("Confirmação de Conta - UFC Diversos");
            message.setText(texto);

            mailSender.send(message);
            logger.info("✅ Email enviado com sucesso via SMTP!");

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail local: {}", e.getMessage());
        }
    }
}