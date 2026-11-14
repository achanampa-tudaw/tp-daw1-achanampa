# TP Integrador DAW1 — Gimnasio

**Alumno/a:** ANABEL CHANAMPA   · **Legajo:** 510134 
**Enunciado elegido:** Gimnasio

## Descripción

API REST desarrollada con Spring Boot para gestionar las actividades de un gimnasio, sus socios y las inscripciones a cada clase, aplicando control de cupos, validación de datos, transiciones de estado y manejo estandarizado de errores.

## Cómo ejecutarlo

Requisito: Java 21. Maven se ejecuta mediante el wrapper incluido en el proyecto.

En Windows PowerShell, desde la raíz del repositorio (`integrador`):

```powershell
.\mvnw.cmd spring-boot:run
```
La API queda disponible en `http://localhost:8080/api`.

- **Consola H2:** `http://localhost:8080/h2-console`
- **URL JDBC:** `jdbc:h2:mem:...` (ver línea `HikariPool-1 - Added connection conn0` en la consola al iniciar la aplicación) · **Usuario:** `sa` · **Contraseña:** vacía.

## Modelo

| Entidad | Atributos principales | Relaciones |
| :--- | :--- | :--- |
| **Actividad** | `id`, `nombre`, `fechaHora`, `cupo` | — |
| **Socio** | `id`, `nombre`, `email` | — |
| **Inscripcion** | `id`, `actividadId`, `socioId`, `estado` | N a 1 con `Actividad` y `Socio` |

**Estados de Inscripción:** toda inscripción nace en estado `CONFIRMADA`. Desde `CONFIRMADA` puede pasar a `ASISTIO` o `CANCELADA`. Tanto `ASISTIO` como `CANCELADA` son estados finales y no permiten transiciones posteriores.

## Endpoints

| Método | Ruta | Descripción | Respuestas principales |
| :--- | :--- | :--- | :--- |
| `GET`, `POST` | `/api/actividades` | Listar actividades (acepta `?fecha=AAAA-MM-DD`) y crear actividad | `200`, `201`, `400` |
| `GET`, `PUT`, `DELETE` | `/api/actividades/{id}` | Consultar, modificar o borrar una actividad | `200`, `204`, `400`, `404`, `409` |
| `GET` | `/api/actividades/{id}/inscripciones` | Listar inscripciones de una actividad | `200`, `404` |
| `GET`, `POST` | `/api/socios` | Listar y crear socios | `200`, `201`, `400` |
| `GET`, `PUT`, `DELETE` | `/api/socios/{id}` | Consultar, modificar o borrar un socio | `200`, `204`, `400`, `404`, `409` |
| `GET`, `POST` | `/api/inscripciones` | Listar inscripciones y registrar una nueva | `200`, `201`, `400`, `404`, `409` |
| `GET` | `/api/inscripciones/{id}` | Consultar una inscripción por ID | `200`, `400`, `404` |
| `PATCH` | `/api/inscripciones/{id}/estado` | Cambiar el estado de una inscripción (`ASISTIO` o `CANCELADA`) | `200`, `400`, `404`, `409` |

## Reglas de negocio

1. **Validación de datos (`400`):** `Actividad` requiere `nombre` no vacío, `fechaHora` futura y `cupo` positivo. `Socio` requiere `nombre` no vacío y `email` válido.
2. **Control de cupo (`409`):** No se permite crear una inscripción si la cantidad de inscripciones no canceladas (`estado <> CANCELADA`) ya alcanzó el `cupo` de la actividad.
3. **Inscripciones duplicadas (`409`):** Un mismo socio no puede inscribirse dos veces a la misma actividad mientras tenga una inscripción activa (no cancelada).
4. **Transiciones de estado (`409`):** Solo se permite cambiar el estado desde `CONFIRMADA` hacia `ASISTIO` o `CANCELADA`.
5. **Anticipación mínima de cancelación (`409`):** No se puede pasar una inscripción a `CANCELADA` si faltan menos de 2 horas para el inicio (`fechaHora`) de la actividad.
6. **Integridad referencial (`409`):** No se puede borrar una actividad ni un socio que tengan inscripciones registradas.

Todos los errores (`400`, `404`, `409`) se devuelven en formato `ProblemDetail` (`status`, `title`, `detail`). Los errores de validación (`400`) incluyen además el mapa `errores` con el detalle por cada campo inválido.

## Pruebas

El archivo `pruebas.http` en la raíz del proyecto contiene peticiones HTTP de ejemplo listas para ejecutar y verificar tanto los flujos exitosos (`200`, `201`, `204`) como los errores esperados (`400` por datos inválidos, `404` por recursos inexistentes y `409` por reglas de negocio).
