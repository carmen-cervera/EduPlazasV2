package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    Optional<Asignacion> findBySolicitanteUsuarioId(Long usuarioId);
}
