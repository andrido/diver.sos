package com.ufc.diversos.service;

import com.ufc.diversos.model.Grupo;
import com.ufc.diversos.repository.GrupoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final ArquivoService arquivoService;

    public GrupoService(GrupoRepository grupoRepository, ArquivoService arquivoService) {
        this.grupoRepository = grupoRepository;
        this.arquivoService = new ArquivoService();
    }

    public List<Grupo> listarTodos() {
        return grupoRepository.findAll();
    }

    public Optional<Grupo> buscarPorId(Long id) {
        return grupoRepository.findById(id);
    }

    public Grupo salvar(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    public boolean deletar(Long id) {
            if (grupoRepository.existsById(id)) {
            grupoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Grupo atualizarFotoGrupo(Long id, org.springframework.web.multipart.MultipartFile arquivo) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        // 1. Salva na pasta "uploads/grupos"
        String caminhoFoto = arquivoService.salvarArquivo(arquivo, "grupos");

        // 2. Atualiza o banco
        grupo.setBannerDoGrupo(caminhoFoto);

        return grupoRepository.save(grupo);
    }
}