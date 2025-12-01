package com.ufc.diversos.controller;

import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    // --- ROTAS GERAIS / ADMINISTRATIVAS ---

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario){
        Usuario novoUsuario = usuarioService.criarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    // Apenas ADMIN/MOD devem acessar (configurado no SecurityConfig)
    @GetMapping
    public List<Usuario> listarUsuarios(){
        return usuarioService.listarUsuarios();
    }

    // Apenas ADMIN/MOD devem acessar (configurado no SecurityConfig)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable int id){
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // O Service já cuida da segurança de quem pode editar quem
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable int id, @RequestBody Usuario usuario){
        // O Service retorna Optional<Usuario>, precisamos do .map para virar ResponseEntity
        return usuarioService.atualizarUsuario(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable int id){
        return usuarioService.deletarUsuario(id) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }

    // --- ROTAS DO PERFIL PESSOAL (/me) ---
    // Essas rotas usam o token para identificar o usuário, sem precisar passar ID na URL

    @GetMapping("/me")
    public ResponseEntity<Usuario> meuPerfil() {
        return ResponseEntity.ok(usuarioService.getUsuarioLogado());
    }

    @GetMapping("/me/vagas")
    public ResponseEntity<List<Vaga>> listarMinhasVagas() {
        return ResponseEntity.ok(usuarioService.listarMinhasVagasSalvas());
    }

    @PostMapping("/me/vagas/{vagaId}")
    public ResponseEntity<Void> salvarVagaFavorita(@PathVariable Long vagaId) {
        usuarioService.salvarVaga(vagaId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/vagas/{vagaId}")
    public ResponseEntity<Void> removerVagaFavorita(@PathVariable Long vagaId) {
        usuarioService.removerVagaSalva(vagaId);
        return ResponseEntity.ok().build(); // Retorna 200 OK
    }
}