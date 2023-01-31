package scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class ScannerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ScannerApplication.class);
		app.run(args);
	}
}
