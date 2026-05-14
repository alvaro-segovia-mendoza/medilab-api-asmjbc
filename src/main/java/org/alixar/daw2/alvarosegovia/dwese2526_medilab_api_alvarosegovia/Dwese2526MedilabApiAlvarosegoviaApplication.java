package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Dwese2526MedilabApiAlvarosegoviaApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dwese2526MedilabApiAlvarosegoviaApplication.class, args);
	}

}
