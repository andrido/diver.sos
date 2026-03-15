package com.ufc.diversos.controller;

import com.ufc.diversos.model.Noticia;
import com.ufc.diversos.service.NoticiaService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noticias")
public class NoticiaController {

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) {
        this.noticiaService = noticiaService;
    }

    @GetMapping
    public ResponseEntity<Page<Noticia>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String titulo) {

        if (titulo != null && !titulo.isBlank()) {
            return ResponseEntity.ok(noticiaService.buscarPorTitulo(titulo, page, size));
        }

        return ResponseEntity.ok(noticiaService.listarTodas(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Noticia> buscarPorId(@PathVariable Long id) {
        return noticiaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Noticia> criar(@RequestBody Noticia noticia) {
        return ResponseEntity.ok(noticiaService.criar(noticia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Noticia> atualizar(@PathVariable Long id, @RequestBody Noticia noticia) {
        return noticiaService.atualizar(id, noticia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return noticiaService.deletar(id) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }
}