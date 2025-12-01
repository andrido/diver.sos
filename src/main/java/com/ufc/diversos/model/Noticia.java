package com.ufc.diversos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT") // Permite textos longos
    private String conteudo;

    private LocalDateTime dataPublicacao;

    // Opcional: Quem postou?
    @ManyToOne
    private Usuario autor;
}