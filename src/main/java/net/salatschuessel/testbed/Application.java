package net.salatschuessel.testbed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ws.config.annotation.EnableWs;

import net.salatschuessel.testbed.webservice.WebserviceBeanProducer;

@SpringBootApplication
//@EnableJms
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableWs
@Import(value = { WebserviceBeanProducer.class })
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
