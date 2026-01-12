package com.ufc.diversos.service;

import com.ufc.diversos.model.Habilidade;
import com.ufc.diversos.model.Vaga;
// Importando os Enums para não precisar escrever Vaga.StatusVaga toda hora no código
import com.ufc.diversos.model.Vaga.StatusVaga;
import com.ufc.diversos.model.Vaga.TipoVaga;
import com.ufc.diversos.model.Vaga.ModalidadeVaga;
import com.ufc.diversos.repository.VagaRepository;
import org.springframework.stereotype.Service;
import com.ufc.diversos.repository.HabilidadeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final ArquivoService arquivoService;
    private final HabilidadeRepository habilidadeRepository;

    public VagaService(VagaRepository vagaRepository, HabilidadeRepository habilidadeRepository, ArquivoService arquivoService) {
        this.vagaRepository = vagaRepository;
        this.arquivoService = new ArquivoService();
        this.habilidadeRepository = habilidadeRepository;
    }
    public List<Vaga> listarTodas() {
        return vagaRepository.findByStatus(StatusVaga.ATIVA);
    }


    public List<Vaga> buscarComFiltros(String termo, ModalidadeVaga modalidade, TipoVaga tipo, String cidade) {
        return vagaRepository.buscarComFiltros(termo, modalidade, tipo, cidade);
    }

    public Optional<Vaga> buscarPorId(Long id) {
        return vagaRepository.findById(id);
    }


    @Transactional
    public Vaga atualizarBannerVaga(Long id, org.springframework.web.multipart.MultipartFile arquivo) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        // 1. Salva na pasta "uploads/vagas"
        String caminhoBanner = arquivoService.salvarArquivo(arquivo, "vagas");

        // 2. Atualiza o banco
        vaga.setLinkDaVaga(caminhoBanner); // Certifique-se que o campo na Model chama 'fotoBanner'

        return vagaRepository.save(vaga);
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

            if (dados.getHabilidades() != null) {
                List<Habilidade> novasHabilidades = new ArrayList<>();

                for (Habilidade h : dados.getHabilidades()) {
                    // Só tentamos buscar se tiver ID
                    if (h.getId() != null) {
                        Optional<Habilidade> habDoBanco = habilidadeRepository.findById(h.getId());

                        if (habDoBanco.isPresent()) {
                            novasHabilidades.add(habDoBanco.get());
                        } else {
                            System.out.println("Habilidade com ID " + h.getId() + " não encontrada.");
                        }
                    }
                }

                //
                v.setHabilidades(novasHabilidades);
            }
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