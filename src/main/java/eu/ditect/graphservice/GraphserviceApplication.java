package eu.ditect.graphservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GraphserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphserviceApplication.class, args);
	}

}
