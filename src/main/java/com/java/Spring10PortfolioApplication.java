package com.java;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import com.java.dtos.RegisterRequest;
import com.java.enums.Role;
import com.java.service.IAuthService;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class Spring10PortfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring10PortfolioApplication.class, args);
	}
	
//	@Bean
//	public CommandLineRunner commandLineRunner(
//			IAuthService service
//	) {
//		return args -> {
//			var admin = RegisterRequest.builder()
//					.firstname("Asif")
//					.lastname("Hassan")
//					.email("asif282@gmail.com")
//					.password("1234")
//					.role(Role.ADMIN)
//					.build();
//			System.out.println("Admin token: " + service.register(admin).getAccessToken());
//
//			var manager = RegisterRequest.builder()
//					.firstname("Ehtishamul")
//					.lastname("Qwerty")
//					.email("qwerty@gmail.com")
//					.password("hacker")
//					.role(Role.VISITOR)
//					.build();
//			System.out.println("Manager token: " + service.register(manager).getAccessToken());
//
//		};
//	}

}
