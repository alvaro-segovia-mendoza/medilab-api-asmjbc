package org.alixar.daw2.alvarosegovia.dwese2526_medilab_api_alvarosegovia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

@SpringBootTest
class Dwese2526MedilabApiAlvarosegoviaApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestSecurityConfig {

		@Bean(name = "jwtKeyPair")
		@Primary
		KeyPair jwtKeyPair() throws Exception {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		}
	}

}
