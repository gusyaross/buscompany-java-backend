package net.buscompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(Config.class)
public class MainApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainApplication.class);

	public static void main(String[] args) {
		try{
			SpringApplication.run(MainApplication.class, args);
		} catch (Exception e) {
			LOGGER.error("Can`t create connection to database, stop");
		}
	}
}

