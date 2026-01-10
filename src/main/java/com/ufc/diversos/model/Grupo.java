package com.ufc.diversos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do grupo é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "O link do grupo é obrigatório")
    private String link;

    @Column(name = "banner_do_grupo")
    private String bannerDoGrupo;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    // --- NOVOS CAMPOS ---
    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    private String estado;

    @NotBlank(message = "O responsável (ou instituição) é obrigatório")
    private String responsavel;
}