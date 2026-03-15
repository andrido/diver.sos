package com.ufc.diversos.repository;

import com.ufc.diversos.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Page<Grupo> findByCategoria(String categoria, Pageable pageable);
}