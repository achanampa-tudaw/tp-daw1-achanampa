package ar.edu.undec.integrador.inscripcion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    @Query("select count(i) from Inscripcion i "
            + "where i.actividad.id = :actividadId and i.estado <> :estado")
    long countByActividadIdAndEstadoNot(
            @Param("actividadId") Long actividadId,
            @Param("estado") EstadoInscripcion estado);

    @Query("select count(i) > 0 from Inscripcion i "
            + "where i.actividad.id = :actividadId and i.socio.id = :socioId "
            + "and i.estado <> :estado")
    boolean existsByActividadIdAndSocioIdAndEstadoNot(
            @Param("actividadId") Long actividadId,
            @Param("socioId") Long socioId,
            @Param("estado") EstadoInscripcion estado);

    @Query("select i from Inscripcion i where i.actividad.id = :actividadId")
    List<Inscripcion> findByActividadId(@Param("actividadId") Long actividadId);

    @Query("select count(i) > 0 from Inscripcion i where i.actividad.id = :actividadId")
    boolean existsByActividadId(@Param("actividadId") Long actividadId);
}