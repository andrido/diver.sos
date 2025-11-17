package com.ufc.diversos.controller;

import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    @Autowired
    private VagaRepository vagaRepository;

    // Listar todas vagas
    @GetMapping
    public List<Vaga> getAll() {
        return vagaRepository.findAll();
    }

    // Buscar vaga por ID
    @GetMapping("/{id}")
    public ResponseEntity<Vaga> getById(@PathVariable Long id) {
        Optional<Vaga> vaga = vagaRepository.findById(id);
        return vaga.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar nova vaga
    @PostMapping
    public Vaga createVaga(@RequestBody Vaga vaga) {
        vaga.setDataCriacao(LocalDateTime.now());
        return vagaRepository.save(vaga);
    }

    // Atualizar vaga existente
    @PutMapping("/{id}")
    public ResponseEntity<Vaga> updateVaga(@PathVariable Long id, @RequestBody Vaga vagaDetails) {
        Optional<Vaga> vagaOptional = vagaRepository.findById(id);
        if (!vagaOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Vaga vaga = vagaOptional.get();
        vaga.setTitulo(vagaDetails.getTitulo());
        vaga.setDescricao(vagaDetails.getDescricao());
        vaga.setEmpresa(vagaDetails.getEmpresa());
        vaga.setStatus(vagaDetails.getStatus());
        vaga.setTipo(vagaDetails.getTipo());
        return ResponseEntity.ok(vagaRepository.save(vaga));
    }

    // Deletar vaga
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaga(@PathVariable Long id) {
        if (!vagaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vagaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
