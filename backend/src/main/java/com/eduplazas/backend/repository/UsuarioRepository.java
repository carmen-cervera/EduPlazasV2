package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByIdEvau(String idEvau);
}