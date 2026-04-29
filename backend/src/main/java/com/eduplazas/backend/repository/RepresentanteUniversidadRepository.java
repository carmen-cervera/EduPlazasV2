package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.RepresentanteUniversidad;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface RepresentanteUniversidadRepository extends CrudRepository<RepresentanteUniversidad, Long> {
    Optional<RepresentanteUniversidad> findByEmailInstitucional(String emailInstitucional);
    boolean existsByEmailInstitucional(String emailInstitucional);
}