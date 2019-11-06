package ru.java.mentor.oldranger.club.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource({
        "classpath:config/datasource.properties",
        "classpath:config/jpa.properties",
        "classpath:config/mail.properties"/*,
        "classpath:config/cache.properties"*/
})
public class AppConfig {
}