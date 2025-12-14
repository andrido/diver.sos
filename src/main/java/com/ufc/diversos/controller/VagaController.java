package com.ufc.diversos.controller;

import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.repository.VagaRepository;
import com.ufc.diversos.service.VagaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @GetMapping
    public List<Vaga> getAll() {
        return vagaService.listarTodas();
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
    public ResponseEntity<Vaga> updateVaga(@PathVariable Long id, @Valid @RequestBody Vaga vagaDetails) {
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
    public List<Vaga> buscarVagas(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Vaga.ModalidadeVaga modalidade,
            @RequestParam(required = false) Vaga.TipoVaga tipo,
            @RequestParam(required = false) String cidade) {

        // Chame o método de busca
        return vagaService.buscarComFiltros(termo, modalidade, tipo, cidade);
    }

}
