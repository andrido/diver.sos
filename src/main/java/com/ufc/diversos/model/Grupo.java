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
    @Column(columnDefinition = "TEXT") // Permite textos longos
    private String descricao;

    @NotBlank(message = "O link do grupo  é obrigatório")
    private String link;

    @NotBlank(message = "A categoria é obrigatória (Ex: TDAH, Estudos, Social)")
    private String categoria;
}