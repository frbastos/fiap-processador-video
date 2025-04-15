package br.com.fiap.processador_video;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ProcessadorVideoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessadorVideoApplication.class, args);
	}

}
