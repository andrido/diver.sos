package com.ufc.diversos.model;

import lombok.Data;

@Data
public class Endereco {
    private String rua;
    private String numero;
    private String bairro;
    private String complemento;
    private String cidade;
    private String estado;
    private String cep;
}
