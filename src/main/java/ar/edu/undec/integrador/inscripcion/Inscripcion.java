package ar.edu.undec.integrador.inscripcion;

import ar.edu.undec.integrador.actividad.Actividad;
import ar.edu.undec.integrador.socio.Socio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Actividad actividad;

    @ManyToOne(optional = false)
    private Socio socio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInscripcion estado = EstadoInscripcion.CONFIRMADA;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio socio) { this.socio = socio; }

    public EstadoInscripcion getEstado() { return estado; }
    public void setEstado(EstadoInscripcion estado) { this.estado = estado; }

    public Long getActividadId() { return actividad.getId(); }
    public Long getSocioId() { return socio.getId(); }
}