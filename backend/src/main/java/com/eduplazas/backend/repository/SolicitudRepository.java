package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Solicitud;
import com.eduplazas.backend.dto.SolicitudRecibidaDTO;
import org.springframework.data.jpa.repository.JpaRepository; // Cambiamos Crud por Jpa
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    
    Optional<Solicitud> findByEstudianteId(Long estudianteId);
    Optional<Solicitud> findByEstudianteIdAndConvocatoriaId(Long estudianteId, Long convocatoriaId);
    List<Solicitud> findByConvocatoriaId(Long convocatoriaId);
    List<Solicitud> findAll();

    
    @Query("SELECT new com.eduplazas.backend.dto.SolicitudRecibidaDTO(" +
           "s.id, " +
           "e.nombre, " + 
           "o.grado, " + 
           "p.ordenPreferencia, " +
           "CAST(s.estado AS string)) " + 
           "FROM Solicitud s " +
           "JOIN s.estudiante e " +
           "JOIN s.preferencias p " +
           "JOIN p.oferta o " +
           "WHERE o.universidad.id = :univId")
    List<SolicitudRecibidaDTO> findSolicitudesByUniversidadId(@Param("univId") Long univId);
}