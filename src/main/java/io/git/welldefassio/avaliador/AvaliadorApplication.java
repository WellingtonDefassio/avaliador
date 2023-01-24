package io.git.welldefassio.avaliador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AvaliadorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvaliadorApplication.class, args);
    }

}
