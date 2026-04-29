package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Convocatoria;
import com.eduplazas.backend.model.EstadoConvocatoriaEnum;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface ConvocatoriaRepository extends CrudRepository<Convocatoria, Long> {
    Optional<Convocatoria> findByEstado(EstadoConvocatoriaEnum estado);
    List<Convocatoria> findAll();
}