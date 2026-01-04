package net.java.springboot_first_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.java.springboot_first_app")
@EnableJpaRepositories(basePackages = "net.java.springboot_first_app.services")
@EntityScan(basePackages = "net.java.springboot_first_app.models")
public class SpringbootFirstAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootFirstAppApplication.class, args);
    }
}
