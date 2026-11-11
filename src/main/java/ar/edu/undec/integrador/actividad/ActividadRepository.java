package ar.edu.undec.integrador.actividad;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    List<Actividad> findByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
            LocalDateTime desde, LocalDateTime hasta);
}