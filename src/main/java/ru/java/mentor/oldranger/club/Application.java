package ru.java.mentor.oldranger.club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.java.mentor.oldranger.club.config.UploadProperties;

@SpringBootApplication
@EnableConfigurationProperties(UploadProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}