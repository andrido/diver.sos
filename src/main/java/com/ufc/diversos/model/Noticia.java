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

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private LocalDateTime dataPublicacao;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @Column(name = "link_externo")
    private String linkExterno;

    @ManyToOne
    private Usuario autor;
}