package com.ufc.diversos.service;

import com.ufc.diversos.model.Habilidade;
import com.ufc.diversos.model.TipoDeUsuario;
import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.model.Vaga;

import com.ufc.diversos.model.Vaga.StatusVaga;
import com.ufc.diversos.model.Vaga.TipoVaga;
import com.ufc.diversos.model.Vaga.ModalidadeVaga;
import com.ufc.diversos.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public VagaService(VagaRepository vagaRepository,
                       HabilidadeRepository habilidadeRepository,
                       ArquivoService arquivoService,
                       UsuarioRepository usuarioRepository,
                       UsuarioService usuarioService) {
        this.vagaRepository = vagaRepository;
        this.arquivoService = arquivoService;
        this.habilidadeRepository = habilidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }
    public List<Vaga> listarVagasAtivas() {
        return vagaRepository.findByStatus(StatusVaga.ATIVA);
    }

    public List<Vaga> listarTodasAsVagas(){
        return vagaRepository.findAll();
    }

    public List<Vaga> listarVagasParaDashboard() {
        Usuario logado = usuarioService.getUsuarioLogado();


        if (logado == null) {
            return new ArrayList<>();
        }

        if (logado.getTipoDeUsuario() == TipoDeUsuario.RH) {

            return vagaRepository.findByCriadorId(logado.getId());
        }

        return vagaRepository.findAll();
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

        String caminhoBanner = arquivoService.salvarArquivo(arquivo, "vagas");


        vaga.setBannerDaVaga(caminhoBanner);

        return vagaRepository.save(vaga);
    }
    @Transactional
    public Vaga criarVaga(Vaga vaga) {
        Usuario logado = usuarioService.getUsuarioLogado();
        vaga.setDataCriacao(LocalDateTime.now());

        vaga.setCriador(logado);
        // RH cadastra vaga INATIVA por padrão
        if (logado.getTipoDeUsuario() == TipoDeUsuario.RH) {
            vaga.setStatus(StatusVaga.INATIVA);
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
        if (vagaRepository.existsById(id)) {
            // 1. Limpa quem favoritou essa vaga
            usuarioRepository.removerVagaDosFavoritos(id);

            // 2. Agora deleta a vaga sem erro
            vagaRepository.deleteById(id);
            return true;
        }
        return false;
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