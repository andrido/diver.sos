package com.ufc.diversos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "habilidades")
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;


    public Habilidade() {}
    public Habilidade(String nome) { this.nome = nome; }
}