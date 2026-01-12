package com.ufc.diversos.repository;

import com.ufc.diversos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuarios_vagas_salvas WHERE vaga_id = :vagaId", nativeQuery = true)
    void removerVagaDosFavoritos(@Param("vagaId") Long vagaId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM usuarios_grupos_salvos WHERE grupo_id = :grupoId", nativeQuery = true)
    void removerGrupoDosFavoritos(@Param("grupoId") Long grupoId);
}

