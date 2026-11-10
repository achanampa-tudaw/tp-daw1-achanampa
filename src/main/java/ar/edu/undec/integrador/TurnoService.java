package ar.edu.undec.integrador;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(@Qualifier("turnoEnMemoria") TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public Turno consultarTurnoPorId(Long id) {
        return turnoRepository.buscarPorId(id);
    }

    public void guardarTurno(Turno turno) {
        turnoRepository.guardar(turno);
    }

}
