package ar.edu.undec.integrador.inscripcion;

import ar.edu.undec.integrador.actividad.Actividad;
import ar.edu.undec.integrador.actividad.ActividadRepository;
import ar.edu.undec.integrador.socio.Socio;
import ar.edu.undec.integrador.socio.SocioRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    public record EstadoRequest(EstadoInscripcion estado) {
    }

    private final InscripcionRepository repository;
    private final ActividadRepository actividadRepository;
    private final SocioRepository socioRepository;

    public InscripcionController(InscripcionRepository repository,
                                 ActividadRepository actividadRepository,
                                 SocioRepository socioRepository) {
        this.repository = repository;
        this.actividadRepository = actividadRepository;
        this.socioRepository = socioRepository;
    }

    @GetMapping
    public List<Inscripcion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Inscripcion obtener(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Inscripcion> crear(@Valid @RequestBody InscripcionRequest request) {
        Actividad actividad = actividadRepository.findById(request.actividadId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Actividad no encontrada"));
        Socio socio = socioRepository.findById(request.socioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Socio no encontrado"));

        long ocupados = repository.countByActividadIdAndEstadoNot(
                actividad.getId(), EstadoInscripcion.CANCELADA);
        if (ocupados >= actividad.getCupo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No hay lugar: el cupo está completo");
        }

        if (repository.existsByActividadIdAndSocioIdAndEstadoNot(
                actividad.getId(), socio.getId(), EstadoInscripcion.CANCELADA)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El socio ya está inscripto a esta actividad");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setActividad(actividad);
        inscripcion.setSocio(socio);
        Inscripcion guardada = repository.save(inscripcion);

        return ResponseEntity
                .created(URI.create("/api/inscripciones/" + guardada.getId()))
                .body(guardada);
    }

    @PatchMapping("/{id}/estado")
    public Inscripcion cambiarEstado(@PathVariable Long id,
                                     @RequestBody EstadoRequest request) {
        if (request.estado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el estado");
        }

        Inscripcion inscripcion = buscar(id);
        EstadoInscripcion nuevo = request.estado();

        boolean transicionValida = inscripcion.getEstado() == EstadoInscripcion.CONFIRMADA
                && (nuevo == EstadoInscripcion.ASISTIO || nuevo == EstadoInscripcion.CANCELADA);
        if (!transicionValida) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transición no permitida: de " + inscripcion.getEstado() + " a " + nuevo);
        }

        if (nuevo == EstadoInscripcion.CANCELADA) {
            LocalDateTime limite = inscripcion.getActividad().getFechaHora().minusHours(2);
            if (!LocalDateTime.now().isBefore(limite)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "No se puede cancelar: faltan menos de 2 horas para la actividad");
            }
        }

        inscripcion.setEstado(nuevo);
        return repository.save(inscripcion);
    }

    private Inscripcion buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
    }
}