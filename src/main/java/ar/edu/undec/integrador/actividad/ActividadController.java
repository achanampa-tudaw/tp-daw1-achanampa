package ar.edu.undec.integrador.actividad;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadRepository repository;

    public ActividadController(ActividadRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Actividad> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (fecha == null) {
            return repository.findAll();
        }
        return repository.findByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }

    @GetMapping("/{id}")
    public Actividad obtener(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Actividad> crear(@Valid @RequestBody Actividad actividad) {
        actividad.setId(null);
        Actividad guardada = repository.save(actividad);
        return ResponseEntity
                .created(URI.create("/api/actividades/" + guardada.getId()))
                .body(guardada);
    }

    @PutMapping("/{id}")
    public Actividad modificar(@PathVariable Long id, @Valid @RequestBody Actividad datos) {
        Actividad actividad = buscar(id);
        actividad.setNombre(datos.getNombre());
        actividad.setFechaHora(datos.getFechaHora());
        actividad.setCupo(datos.getCupo());
        return repository.save(actividad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        buscar(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Actividad buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }
}