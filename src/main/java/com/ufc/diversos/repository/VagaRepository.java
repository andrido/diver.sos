package com.ufc.diversos.repository;

import com.ufc.diversos.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByTipo(Vaga.TipoVaga tipo);
    List<Vaga> findByStatusAndTipo(Vaga.StatusVaga status, Vaga.TipoVaga tipo);

}
