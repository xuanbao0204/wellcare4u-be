package vn.wellcare4u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Wellcare4uApplication {

	public static void main(String[] args) {
		SpringApplication.run(Wellcare4uApplication.class, args);
	}

}
