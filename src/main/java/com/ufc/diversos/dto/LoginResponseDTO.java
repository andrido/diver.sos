package com.ufc.diversos.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String role;
    private String nome;

    public LoginResponseDTO(String token, String role, String nome) {
        this.token = token;
        this.role = role;
        this.nome = nome;
    }
}