package com.ufc.diversos.controller;

import com.ufc.diversos.model.Habilidade;
import com.ufc.diversos.repository.HabilidadeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habilidades")
public class HabilidadeController {

    private final HabilidadeRepository repository;

    public HabilidadeController(HabilidadeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Habilidade> listarTodas() {
        return repository.findAll();
    }


    @PostMapping
    public Habilidade criar(@RequestBody Habilidade habilidade) {
        return repository.save(habilidade);
    }
}