package com.ufc.diversos.controller;

import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.service.VagaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page; // Importante ser este
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }


    @GetMapping
    public ResponseEntity<Page<Vaga>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(vagaService.listarTodas(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> getById(@PathVariable Long id) {
        return vagaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vaga> createVaga(@Valid @RequestBody Vaga vaga) {
        return ResponseEntity.ok(vagaService.criar(vaga));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vaga> updateVaga(@PathVariable Long id, @RequestBody Vaga vagaDetails) {
        return vagaService.atualizar(id, vagaDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaga(@PathVariable Long id) {
        return vagaService.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }


    @GetMapping("/buscar")
    public ResponseEntity<Page<Vaga>> buscarVagas(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Vaga.ModalidadeVaga modalidade,
            @RequestParam(required = false) Vaga.TipoVaga tipo,
            @RequestParam(required = false) String cidade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(vagaService.buscarComFiltros(termo, modalidade, tipo, cidade, page, size));
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<Vaga> uploadBanner(@PathVariable Long id,
                                             @RequestParam("arquivo") MultipartFile arquivo) {
        Vaga vagaAtualizada = vagaService.atualizarBannerVaga(id, arquivo);
        return ResponseEntity.ok(vagaAtualizada);
    }
}