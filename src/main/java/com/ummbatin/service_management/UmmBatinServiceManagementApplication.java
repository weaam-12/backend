package com.ummbatin.service_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@SpringBootApplication
public class UmmBatinServiceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmmBatinServiceManagementApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


	}
	@Configuration
	public class StaticResourceConfig implements WebMvcConfigurer {
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/uploads/**")
					.addResourceLocations("file:" + Paths.get("uploads").toAbsolutePath() + "/");
		}
	}
}