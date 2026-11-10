package ar.edu.undec.integrador;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;


@Repository("turnoEnDB")
public class TurnoRepositoryEnDBImp implements TurnoRepository {
    @Override
    public Turno buscarPorId(Long id) {
        return new Turno(id, "Juan Torres", "09:30 - 10:00");
    }

    @Override
    public void guardar(Turno turno) {
        System.out.println("Guardando turno en la base de datos: " + turno.getNombre() + ", Horario: " + turno.getHorario());
    }

    @Override
    public Turno buscarPorNombre(String nombre) {
        return new Turno(1L, nombre, "09:30 - 10:00");
    }
}
