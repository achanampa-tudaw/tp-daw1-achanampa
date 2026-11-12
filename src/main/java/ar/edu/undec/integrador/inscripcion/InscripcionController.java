package ar.edu.undec.integrador.inscripcion;

import ar.edu.undec.integrador.actividad.Actividad;
import ar.edu.undec.integrador.actividad.ActividadRepository;
import ar.edu.undec.integrador.socio.Socio;
import ar.edu.undec.integrador.socio.SocioRepository;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

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

    private Inscripcion buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
    }
}