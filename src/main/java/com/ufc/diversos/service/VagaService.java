package com.ufc.diversos.service;

import com.ufc.diversos.model.Vaga;
// Importando os Enums para não precisar escrever Vaga.StatusVaga toda hora no código
import com.ufc.diversos.model.Vaga.StatusVaga;
import com.ufc.diversos.model.Vaga.TipoVaga;
import com.ufc.diversos.model.Vaga.ModalidadeVaga;
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


    public List<Vaga> buscarComFiltros(String termo, ModalidadeVaga modalidade, TipoVaga tipo, String cidade) {
        return vagaRepository.buscarComFiltros(termo, modalidade, tipo, cidade);
    }

    public Optional<Vaga> buscarPorId(Long id) {
        return vagaRepository.findById(id);
    }



    public Vaga criar(Vaga vaga) {

        vaga.setDataCriacao(LocalDateTime.now());

        if (vaga.getStatus() == null) {
            vaga.setStatus(StatusVaga.ATIVA);
        }


        return vagaRepository.save(vaga);
    }

    public Optional<Vaga> atualizar(Long id, Vaga dados) {
        return vagaRepository.findById(id).map(v -> {

            atualizarSeValido(dados.getTitulo(), v::setTitulo);
            atualizarSeValido(dados.getDescricao(), v::setDescricao);
            atualizarSeValido(dados.getEmpresa(), v::setEmpresa);
            atualizarSeValido(dados.getLinkDaVaga(), v::setLinkDaVaga);
            atualizarSeValido(dados.getCidade(), v::setCidade);


            atualizarSePresente(dados.getDataLimite(), v::setDataLimite);
            atualizarSePresente(dados.getStatus(), v::setStatus);
            atualizarSePresente(dados.getTipo(), v::setTipo);
            atualizarSePresente(dados.getModalidade(), v::setModalidade);

            return vagaRepository.save(v);
        });
    }
    public boolean deletar(Long id) {
        if (!vagaRepository.existsById(id)) return false;
        vagaRepository.deleteById(id);
        return true;
    }

    private void atualizarSeValido(String valor, java.util.function.Consumer<String> setter) {
        if (valor != null && !valor.isBlank()) {
            setter.accept(valor);
        }
    }

    private <T> void atualizarSePresente(T valor, java.util.function.Consumer<T> setter) {
        if (valor != null) {
            setter.accept(valor);
        }
    }


}