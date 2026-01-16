package com.ufc.diversos.dto;

import lombok.Data;

@Data
public class ResetSenhaRequestDTO{
    private String senhaAtual;
    private String novaSenha;
    private String novaSenhaRepeticao;
}