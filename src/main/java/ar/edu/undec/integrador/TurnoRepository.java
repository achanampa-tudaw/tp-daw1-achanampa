package ar.edu.undec.integrador;

import org.springframework.stereotype.Repository;

@Repository
public interface TurnoRepository {
    Turno buscarPorId(Long id);
    void guardar(Turno turno);
    Turno buscarPorNombre(String nombre);
}
