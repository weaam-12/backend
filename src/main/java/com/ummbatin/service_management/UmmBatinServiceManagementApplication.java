package com.ummbatin.service_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UmmBatinServiceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmmBatinServiceManagementApplication.class, args);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		

	}

}
