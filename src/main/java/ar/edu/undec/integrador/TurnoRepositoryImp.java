package ar.edu.undec.integrador;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository("turnoEnMemoria")
public class TurnoRepositoryImp implements TurnoRepository{

    private final Map<Long, Turno> tabla = new HashMap<>();

    public TurnoRepositoryImp() {
        tabla.put(1L, new Turno(1L, "German Torres", "09:30 - 10:00"));
        tabla.put(2L, new Turno(2L, "Javier Ruitti", "12:00 - 16:00"));
    }

    @Override
    public Turno buscarPorId(Long id) {
        // Implementación de la búsqueda por ID
        return tabla.get(id);
    }

    @Override
    public void guardar(Turno turno) {
        tabla.put(turno.getId(), turno);
        System.out.println("Guardando turno: " + turno.getNombre() + ", Horario: " + turno.getHorario());
    }

    @Override
    public Turno buscarPorNombre(String nombre) {
        return tabla.values().stream()
                .filter(t -> t.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }

}
