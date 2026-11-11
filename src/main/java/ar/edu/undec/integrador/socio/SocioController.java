package ar.edu.undec.integrador.socio;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/socios")
public class SocioController {

    private final SocioRepository repository;

    public SocioController(SocioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Socio> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Socio obtener(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<Socio> crear(@Valid @RequestBody Socio socio) {
        socio.setId(null);
        Socio guardado = repository.save(socio);
        return ResponseEntity
                .created(URI.create("/api/socios/" + guardado.getId()))
                .body(guardado);
    }

    @PutMapping("/{id}")
    public Socio modificar(@PathVariable Long id, @Valid @RequestBody Socio datos) {
        Socio socio = buscar(id);
        socio.setNombre(datos.getNombre());
        socio.setEmail(datos.getEmail());
        return repository.save(socio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        buscar(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Socio buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Socio no encontrado"));
    }
}