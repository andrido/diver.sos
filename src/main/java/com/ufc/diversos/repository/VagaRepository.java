package com.ufc.diversos.repository;

import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.model.Vaga.*;
import org.apache.kafka.common.metrics.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {


    @Query("SELECT DISTINCT v FROM Vaga v LEFT JOIN v.habilidades h WHERE " +
            "v.status = 'ATIVA' AND " +  // <--- ADICIONEI ISSO: Só traz vaga ativa
            "(:termo IS NULL OR LOWER(v.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(v.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(v.empresa) LIKE LOWER(CONCAT('%', :termo, '%')) " +
            "OR LOWER(h.nome) LIKE LOWER(CONCAT('%', :termo, '%'))) AND " +
            "(:modalidade IS NULL OR v.modalidade = :modalidade) AND " +
            "(:tipo IS NULL OR v.tipo = :tipo) AND " +
            "(:cidade IS NULL OR LOWER(v.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))")
    List<Vaga> buscarComFiltros(
            @Param("termo") String termo,
            @Param("modalidade") ModalidadeVaga modalidade,
            @Param("tipo") TipoVaga tipo,
            @Param("cidade") String cidade);

    List<Vaga> findByStatus(StatusVaga status);
}


