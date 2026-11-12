package ar.edu.undec.integrador.actividad;

import ar.edu.undec.integrador.inscripcion.Inscripcion;
import ar.edu.undec.integrador.inscripcion.InscripcionRepository;
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
    private final InscripcionRepository inscripcionRepository;

    public ActividadController(ActividadRepository repository,
                               InscripcionRepository inscripcionRepository) {
        this.repository = repository;
        this.inscripcionRepository = inscripcionRepository;
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

    @GetMapping("/{id}/inscripciones")
    public List<Inscripcion> inscripciones(@PathVariable Long id) {
        buscar(id);
        return inscripcionRepository.findByActividadId(id);
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
        if (inscripcionRepository.existsByActividadId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La actividad tiene inscripciones y no se puede borrar");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Actividad buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
    }
}