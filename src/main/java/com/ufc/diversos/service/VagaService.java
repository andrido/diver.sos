package com.ufc.diversos.service;


import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
    public class VagaService {

        private final VagaRepository vagaRepository;

        public VagaService(VagaRepository vagaRepository) {
            this.vagaRepository = vagaRepository;
        }

        public List<Vaga> listarTodas() {
            return vagaRepository.findAll();
        }

        public Optional<Vaga> buscarPorId(Long id) {
            return vagaRepository.findById(id);
        }

        public Vaga criar(Vaga vaga) {
            vaga.setDataCriacao(LocalDateTime.now());
            return vagaRepository.save(vaga);
        }

        public Optional<Vaga> atualizar(Long id, Vaga dados) {
            return vagaRepository.findById(id).map(v -> {
                v.setTitulo(dados.getTitulo());
                v.setDescricao(dados.getDescricao());
                v.setEmpresa(dados.getEmpresa());
                v.setStatus(dados.getStatus());
                v.setTipo(dados.getTipo());
                return vagaRepository.save(v);
            });
        }

        public boolean deletar(Long id) {
            if (!vagaRepository.existsById(id)) return false;
            vagaRepository.deleteById(id);
            return true;
        }
    }


