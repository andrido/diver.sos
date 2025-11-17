package com.ufc.diversos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario implements IUsuario {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private int id;

 private String nome;
 private String email;
 private String username;
 private String senha;
 private String telefone;
 private String cpf;

 @Embedded
 private Endereco endereco;

 @Enumerated(EnumType.STRING)
 private statusUsuario status;

 @Enumerated(EnumType.STRING)
 private tipoDeUsuario tipoDeUsuario;
}
