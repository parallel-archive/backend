package hu.codeandsoda.osa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableElasticsearchRepositories
public class OsaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsaBackendApplication.class, args);
	}

}
