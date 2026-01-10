package com.ufc.diversos.repository;

import com.ufc.diversos.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    // Método mágico do JPA para achar pelo texto do token
    Optional<VerificationToken> findByToken(String token);
}