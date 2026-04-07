package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findAllBySolicitanteUsuarioId(Long usuarioId);
    List<Asignacion> findAllBySolicitanteId(Long solicitanteId);
}
