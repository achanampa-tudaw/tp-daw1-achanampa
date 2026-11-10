package ar.edu.undec.integrador;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class TurnoRepositoryMySQL implements TurnoRepository {
    @Override
    public Turno buscarPorId(Long id) {
        return new Turno(id, "MySQL User", "09:30 - 10:00");
    }

    @Override
    public void guardar(Turno turno) {
        System.out.println("Guardando turno en MySQL: " + turno.getNombre() + ", Horario: " + turno.getHorario());
    }

    @Override
    public Turno buscarPorNombre(String nombre) {
        return new Turno(1L, nombre, "09:30 - 10:00");
    }
}
