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

    // NOVO CAMPO
    @Column(nullable = true)
    private String linkDaVaga;

    @Column(nullable = false)
    private String cidade; // NOVO CAMPO para filtro

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    // NOVO CAMPO: Data limite para inscrição
    @Column(nullable = true)
    private LocalDateTime dataLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVaga status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVaga tipo;

    // NOVO ENUM: Modalidade
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeVaga modalidade;

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

    public enum ModalidadeVaga {
        PRESENCIAL,
        REMOTO,
        HIBRIDO
    }
}
