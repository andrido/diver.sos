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

    // Pega o email do remetente direto do application.yml para não hardcoded
    @Value("${spring.mail.username}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async // Roda em segundo plano
    public void enviarEmailConfirmacao(String emailDestino, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(emailDestino);
            message.setSubject("Confirmação de Conta - UFC Diversos");

            // Link apontando para o seu Backend (que pode redirecionar pro front)
            // Ou apontando direto pro Front se preferir: http://localhost:5173/confirmar?token=...
            String link = "http://localhost:8080/auth/confirmar?token=" + token;

            String texto = """
                    Olá!
                    
                    Seja bem-vindo à Diver.SOS.
                    Para ativar sua conta e liberar seu acesso, clique no link abaixo:
                    
                    %s
                    
                    Se você não solicitou este cadastro, apenas ignore este e-mail.
                    """.formatted(link);

            message.setText(texto);

            mailSender.send(message);
            logger.info("E-mail de confirmação enviado com sucesso para: {}", emailDestino);

        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail para {}: {}", emailDestino, e.getMessage());
        }
    }
}