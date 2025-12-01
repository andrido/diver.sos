package com.ufc.diversos.repository;

import com.ufc.diversos.model.Vaga;
// IMPORTANTE: Importar os Enums que estão dentro de Vaga
import com.ufc.diversos.model.Vaga.ModalidadeVaga;
import com.ufc.diversos.model.Vaga.TipoVaga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {


    @Query("SELECT v FROM Vaga v WHERE " +
            "(:termo IS NULL OR LOWER(v.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(v.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))) AND " +
            "(:modalidade IS NULL OR v.modalidade = :modalidade) AND " +
            "(:tipo IS NULL OR v.tipo = :tipo) AND " +
            "(:cidade IS NULL OR LOWER(v.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))")
    List<Vaga> buscarComFiltros(
            @Param("termo") String termo,
            @Param("modalidade") ModalidadeVaga modalidade,
            @Param("tipo") TipoVaga tipo,
            @Param("cidade") String cidade);
}