package com.ufc.diversos.service;

import com.ufc.diversos.model.Noticia;
import com.ufc.diversos.repository.NoticiaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final UsuarioService usuarioService; // Necessário para setar o Autor

    public NoticiaService(NoticiaRepository noticiaRepository, UsuarioService usuarioService) {
        this.noticiaRepository = noticiaRepository;
        this.usuarioService = usuarioService;
    }

    public List<Noticia> listarTodas() {
        return noticiaRepository.findAll();
    }

    public Optional<Noticia> buscarPorId(Long id) {
        return noticiaRepository.findById(id);
    }

    public Noticia criar(Noticia noticia) {
        // Validações básicas
        if (noticia.getTitulo() == null || noticia.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título da notícia é obrigatório.");
        }
        if (noticia.getConteudo() == null || noticia.getConteudo().isBlank()) {
            throw new IllegalArgumentException("O conteúdo da notícia é obrigatório.");
        }

        noticia.setDataPublicacao(LocalDateTime.now());

        // Define quem está postando (pega do Token JWT)
        noticia.setAutor(usuarioService.getUsuarioLogado());

        return noticiaRepository.save(noticia);
    }

    public Optional<Noticia> atualizar(Long id, Noticia dados) {
        return noticiaRepository.findById(id).map(n -> {

            if (dados.getTitulo() != null && !dados.getTitulo().isBlank()) {
                n.setTitulo(dados.getTitulo());
            }

            // Aqui usamos 'conteudo' conforme sua Model
            if (dados.getConteudo() != null && !dados.getConteudo().isBlank()) {
                n.setConteudo(dados.getConteudo());
            }

            // Nota: Não atualizamos o autor nem a data de publicação na edição

            return noticiaRepository.save(n);
        });
    }

    public boolean deletar(Long id) {
        if (noticiaRepository.existsById(id)) {
            noticiaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}