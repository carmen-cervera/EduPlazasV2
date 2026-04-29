package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByDni(String dni);
    boolean existsByDni(String dni);
}