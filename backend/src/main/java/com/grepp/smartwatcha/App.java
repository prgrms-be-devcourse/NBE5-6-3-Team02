package com.grepp.smartwatcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableNeo4jRepositories
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
