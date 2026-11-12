package ar.edu.undec.integrador.inscripcion;

import jakarta.validation.constraints.NotNull;

public record InscripcionRequest(
        @NotNull Long actividadId,
        @NotNull Long socioId) {
}