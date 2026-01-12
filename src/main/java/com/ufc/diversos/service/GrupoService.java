package com.ufc.diversos.service;

import com.ufc.diversos.model.Grupo;
import com.ufc.diversos.repository.GrupoRepository;
import com.ufc.diversos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final ArquivoService arquivoService;
    private final UsuarioRepository usuarioRepository;

    // CORREÇÃO 1: Faltava a chave '{' aqui no começo
    public GrupoService(GrupoRepository grupoRepository,
                        ArquivoService arquivoService,
                        UsuarioRepository usuarioRepository) {
        this.grupoRepository = grupoRepository;
        // CORREÇÃO 2: Usa o serviço injetado pelo Spring, não cria um 'new'
        this.arquivoService = arquivoService;
        this.usuarioRepository = usuarioRepository;
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

    // CORREÇÃO 3: Adicionado @Transactional e a limpeza dos favoritos
    @Transactional
    public boolean deletar(Long id) {
        if (grupoRepository.existsById(id)) {
            // 1. Limpa quem favoritou esse grupo antes de apagar
            usuarioRepository.removerGrupoDosFavoritos(id);

            // 2. Agora deleta o grupo sem erro de constraint
            grupoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Grupo atualizar(Long id, Grupo dadosNovos) {
        // 1. Busca o dado antigo no banco
        Grupo grupoExistente = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        // 2. DEBUG
        System.out.println("Recebido para atualizar: " + dadosNovos.getNome());

        // 3. Atualiza TODOS os campos
        grupoExistente.setNome(dadosNovos.getNome());
        grupoExistente.setDescricao(dadosNovos.getDescricao());
        grupoExistente.setLink(dadosNovos.getLink());
        grupoExistente.setCategoria(dadosNovos.getCategoria());
        grupoExistente.setCidade(dadosNovos.getCidade());
        grupoExistente.setEstado(dadosNovos.getEstado());
        grupoExistente.setResponsavel(dadosNovos.getResponsavel());

        // Se a foto vier como string (URL) no JSON e não via upload separado:
        if (dadosNovos.getBannerDoGrupo() != null) {
            grupoExistente.setBannerDoGrupo(dadosNovos.getBannerDoGrupo());
        }

        // 4. Salva as alterações
        return grupoRepository.save(grupoExistente);
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