package ru.kata.spring.boot_security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
		openHomePage();
	}

	// автоматически открывает адрес http://localhost:8080/ сразу после старта приложения
	private static void openHomePage() throws IOException {
		Runtime rt = Runtime.getRuntime();
		rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://localhost:8080/");
	}

}
