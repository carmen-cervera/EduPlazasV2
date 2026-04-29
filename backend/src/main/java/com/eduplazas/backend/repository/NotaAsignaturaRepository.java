package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.NotaAsignatura;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NotaAsignaturaRepository extends CrudRepository<NotaAsignatura, Long> {
    List<NotaAsignatura> findByEstudianteId(Long estudianteId);
    void deleteByEstudianteId(Long estudianteId);
}