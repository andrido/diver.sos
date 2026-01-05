package com.ufc.diversos.repository;

import com.ufc.diversos.model.Habilidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {
    // Para evitar duplicatas na hora de cadastrar
    boolean existsByNome(String nome);
}