package ru.java.mentor.oldranger.club.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource({
        "classpath:config/datasource.properties.sample",
        "classpath:config/jpa.properties.sample",
        "classpath:config/mail.properties.sample"/*,
        "classpath:config/cache.properties"*/
})
public class AppConfig {
}