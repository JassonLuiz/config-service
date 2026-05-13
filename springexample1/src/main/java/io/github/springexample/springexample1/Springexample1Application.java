package io.github.springexample.springexample1;

import io.github.clientlibrary.client_library.EnableConfigClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigClient
public class Springexample1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springexample1Application.class, args);
	}

}
