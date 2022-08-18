package be.mkfin.messandcantine;

import be.mkfin.messandcantine.entity.UserRegistered;
import be.mkfin.messandcantine.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MessandcantineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessandcantineApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
		return (args) -> {
			if (userRepository.findByUsername("admin") == null) {
				System.out.println("Creating Administrator !!");
				UserRegistered user = new UserRegistered();
				user.setUsername("admin");
				user.setFirstName("Yassine");
				user.setLastName("Makra");
				user.setMail("admin@gmail.com");
				user.setPassword(encoder.encode("XXXXXX"));
				user.setRole(UserRegistered.Role.ADMIN);
				user.setActive(true);
				userRepository.save(user);
			}
		};
	}
	

}
