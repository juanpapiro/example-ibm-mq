package com.example.ibmmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class IbmmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(IbmmqApplication.class, args);
	}

}
