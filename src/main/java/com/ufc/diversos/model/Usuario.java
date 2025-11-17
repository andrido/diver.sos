package com.ufc.diversos.model;


import lombok.Data;

@Data
public class Usuario implements IUsuario{
 private int id;
 private String nome;
 private String email;
 private String username;
 private String senha;
 private String telefone;
 private String cpf;
 private Endereco endereco;
 private statusUsuario status;
 private tipoDeUsuario tipoDeUsuario;

}
