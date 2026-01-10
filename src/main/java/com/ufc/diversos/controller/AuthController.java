package com.ufc.diversos.controller;

import com.ufc.diversos.dto.LoginDTO;
import com.ufc.diversos.dto.LoginResponseDTO;
import com.ufc.diversos.model.StatusUsuario; // Importe o Enum
import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.repository.UsuarioRepository;
import com.ufc.diversos.security.JwtTokenProvider;
import com.ufc.diversos.service.UsuarioService; // Importe o Service
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService; // <--- Adicionei o Service

    // Adicione o service no construtor
    public AuthController(UsuarioRepository usuarioRepository,
                          BCryptPasswordEncoder encoder,
                          JwtTokenProvider tokenProvider,
                          UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.tokenProvider = tokenProvider;
        this.usuarioService = usuarioService;
    }

    // ---  ROTA DE CONFIRMAÇÃO  ---
    @GetMapping("/confirmar")
    public ResponseEntity<String> confirmar(@RequestParam("token") String token) {
        try {
            String mensagem = usuarioService.confirmarConta(token);
            return ResponseEntity.ok(mensagem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO dto) {

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com este e-mail"));

        if (!encoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        // --- BLOQUEAR LOGIN SE NÃO FOR ATIVO ---
        if (usuario.getStatus() != StatusUsuario.ATIVO) {
            throw new RuntimeException("Sua conta está inativa ou bloqueada. Verifique seu e-mail para confirmar o cadastro.");
        }

        String token = tokenProvider.gerarToken(usuario);

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                usuario.getTipoDeUsuario().name(),
                usuario.getNome()
        );

        return ResponseEntity.ok(response);
    }
}