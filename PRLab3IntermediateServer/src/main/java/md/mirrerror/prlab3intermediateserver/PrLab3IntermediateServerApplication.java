package md.mirrerror.prlab3intermediateserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PrLab3IntermediateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrLab3IntermediateServerApplication.class, args);
    }

}