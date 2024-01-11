package site.goldenticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GoldenTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoldenTicketApplication.class, args);
	}

}
