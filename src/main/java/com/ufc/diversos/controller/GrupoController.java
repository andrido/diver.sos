package com.ufc.diversos.controller;

import com.ufc.diversos.model.Grupo;
import com.ufc.diversos.service.GrupoService;
import jakarta.validation.Valid; // Importante para o @NotBlank funcionar
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    // Listar todos (Público)
    @GetMapping
    public List<Grupo> listar() {
        return grupoService.listarTodos();
    }

    // Buscar um (Público)
    @GetMapping("/{id}")
    public ResponseEntity<Grupo> buscarPorId(@PathVariable Long id) {
        return grupoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar (Restrito a Admin/Mod)
    @PostMapping
    public ResponseEntity<Grupo> criar(@Valid @RequestBody Grupo grupo) {
        return ResponseEntity.ok(grupoService.salvar(grupo));
    }

    // Deletar (Restrito a Admin/Mod)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return grupoService.deletar(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<Grupo> uploadFoto(@PathVariable Long id,
                                            @RequestParam("arquivo") org.springframework.web.multipart.MultipartFile arquivo) {

        Grupo grupoAtualizado = grupoService.atualizarFotoGrupo(id, arquivo);

        return ResponseEntity.ok(grupoAtualizado);
    }
}