package com.ufc.diversos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    private LocalDateTime dataExpiracao;

    // Construtor que gera o token automaticamente
    public VerificationToken(Usuario usuario) {
        this.usuario = usuario;
        this.dataExpiracao = LocalDateTime.now().plusHours(24); // Token vale por 24h
        this.token = UUID.randomUUID().toString();
    }
}