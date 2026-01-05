package com.ufc.diversos.controller;

import com.ufc.diversos.dto.LoginDTO;
import com.ufc.diversos.dto.LoginResponseDTO; // Importe o DTO novo
import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.repository.UsuarioRepository;
import com.ufc.diversos.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity; // Importante
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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {


        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com este e-mail"));


        if (!encoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }


        String token = tokenProvider.gerarToken(usuario);

        // 4. Retorna o objeto completo (Token + Role + Nome)
        LoginResponseDTO response = new LoginResponseDTO(
                token,
                usuario.getTipoDeUsuario().name(),
                usuario.getNome()
        );

        return ResponseEntity.ok(response);
    }
}