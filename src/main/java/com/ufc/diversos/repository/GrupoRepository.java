package com.ufc.diversos.repository;

import com.ufc.diversos.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByCategoria(String categoria);
}