package com.ufc.diversos.controller;

import com.ufc.diversos.dto.LoginDTO;
import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.repository.UsuarioRepository;
import com.ufc.diversos.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UsuarioRepository usuarioRepository, BCryptPasswordEncoder encoder, JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!encoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        return tokenProvider.gerarToken(usuario);
    }
}
