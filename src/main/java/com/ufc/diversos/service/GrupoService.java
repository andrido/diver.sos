package com.ufc.diversos.service;

import com.ufc.diversos.model.Grupo;
import com.ufc.diversos.repository.GrupoRepository;
import com.ufc.diversos.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final ArquivoService arquivoService;
    private final UsuarioRepository usuarioRepository;


    public GrupoService(GrupoRepository grupoRepository, ArquivoService arquivoService, UsuarioRepository usuarioRepository) {
        this.grupoRepository = grupoRepository;
        this.arquivoService = arquivoService;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Grupo> listarTodos(int pagina, int tamanho) {
        // Ordenando por nome por padrão, já que é uma listagem de grupos
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome").ascending());
        return grupoRepository.findAll(pageable);
    }
    public Page<Grupo> buscarPorCategoria(String categoria, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome").ascending());
        return grupoRepository.findByCategoria(categoria, pageable);
    }

    public Optional<Grupo> buscarPorId(Long id) {
        return grupoRepository.findById(id);
    }

    public Grupo salvar(Grupo grupo) {
        return grupoRepository.save(grupo);
    }


    @Transactional
    public boolean deletar(Long id) {
        if (grupoRepository.existsById(id)) {

            usuarioRepository.removerGrupoDosFavoritos(id);


            grupoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Grupo atualizar(Long id, Grupo dadosNovos) {
        Grupo grupoExistente = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        grupoExistente.setNome(dadosNovos.getNome());
        grupoExistente.setDescricao(dadosNovos.getDescricao());
        grupoExistente.setLink(dadosNovos.getLink());
        grupoExistente.setCategoria(dadosNovos.getCategoria());
        grupoExistente.setCidade(dadosNovos.getCidade());
        grupoExistente.setEstado(dadosNovos.getEstado());
        grupoExistente.setResponsavel(dadosNovos.getResponsavel());


        if (dadosNovos.getBannerDoGrupo() != null) {
            grupoExistente.setBannerDoGrupo(dadosNovos.getBannerDoGrupo());
        }


        return grupoRepository.save(grupoExistente);
    }

    @Transactional
    public Grupo atualizarFotoGrupo(Long id, org.springframework.web.multipart.MultipartFile arquivo) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        String caminhoFoto = arquivoService.salvarArquivo(arquivo, "grupos");

        grupo.setBannerDoGrupo(caminhoFoto);

        return grupoRepository.save(grupo);
    }
}