package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Estudiante;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface EstudianteRepository extends CrudRepository<Estudiante, Long> {
    Optional<Estudiante> findByEmail(String email);
    Optional<Estudiante> findByIdEvau(String idEvau);
    boolean existsByEmail(String email);
    boolean existsByIdEvau(String idEvau);
}