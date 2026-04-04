ackage com.eduplazas.backend.controller;

import com.eduplazas.backend.model.Asignacion;
import com.eduplazas.backend.service.AsignacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    // Lanzar el proceso de asignación
    @PostMapping("/procesar")
    public ResponseEntity<String> procesarAsignaciones() {
        asignacionService.procesarAsignaciones();
        return ResponseEntity.ok("Asignación procesada correctamente");
    }

    // Ver todos los resultados de la asignación
    @GetMapping
    public ResponseEntity<List<Asignacion>> obtenerResultados() {
        return ResponseEntity.ok(asignacionService.obtenerTodas());
    }

 // Ver la asignación de un estudiante concreto
    @GetMapping("/mi-asignacion/{usuarioId}")
    public ResponseEntity<?> obtenerMiAsignacion(@PathVariable Long usuarioId) {
        Optional<Asignacion> asignacion = asignacionService.obtenerPorUsuario(usuarioId);
        if (asignacion.isPresent()) {
            return ResponseEntity.ok(asignacion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
}


}