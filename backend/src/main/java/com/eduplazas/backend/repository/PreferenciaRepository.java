package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Preferencia;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PreferenciaRepository extends CrudRepository<Preferencia, Long> {
    List<Preferencia> findBySolicitudIdOrderByOrdenPreferencia(Long solicitudId);
    List<Preferencia> findByOfertaId(Long ofertaId);
}