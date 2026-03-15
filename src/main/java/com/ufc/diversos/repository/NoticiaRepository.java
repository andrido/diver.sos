package com.ufc.diversos.repository;

import com.ufc.diversos.model.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    // Busca paginada por título para a barra de pesquisa do portal
    Page<Noticia> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
}