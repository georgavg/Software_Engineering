package TraineeshipApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "TraineeshipApplication")
@EntityScan(basePackages = "TraineeshipApplication.model")  // Σιγουρεύεται ότι οι οντότητες γίνονται scan
@EnableJpaRepositories(basePackages = "TraineeshipApplication.dao")  // Βεβαιώνει ότι τα Repositories είναι γνωστά
public class Application{
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
