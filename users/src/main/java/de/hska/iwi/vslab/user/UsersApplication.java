package de.hska.iwi.vslab.user;

import de.hska.iwi.vslab.user.model.User;
import de.hska.iwi.vslab.user.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class UsersApplication {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	@PostConstruct
	public void generateTestData() {
		userRepo.save(new User("Maxiboi", "Max", "Mustermann", this.passwordEncoder.encode("sicheresPasswort"), false));
		userRepo.save(new User("Adminflieger", "Ad", "Min", this.passwordEncoder.encode("keinSicheresPasswort"), true));
		userRepo.save(new User("hackerman", "nota", "hacker", this.passwordEncoder.encode("123456"), false));
	}


	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(UsersApplication.class, args);
	}

}