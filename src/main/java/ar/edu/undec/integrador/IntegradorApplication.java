package ar.edu.undec.integrador;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntegradorApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegradorApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(TurnoService turnoService) {
		return args -> {

		Turno turno = turnoService.consultarTurnoPorId(1L);
		System.out.println("Turno consultado: " + turno.getNombre() + ", Horario: " + turno.getHorario());
		};
	}
}


