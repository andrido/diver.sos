package com.ufc.diversos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vaga")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private String empresa;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVaga status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVaga tipo;

    public enum StatusVaga {
        ATIVA,
        PREENCHIDA
    }

    public enum TipoVaga {
        AFIRMATIVA,
        NAO_AFIRMATIVA,
        EDITAL,
        NAO_EDITAL
    }
}
