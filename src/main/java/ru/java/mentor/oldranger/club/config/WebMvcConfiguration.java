package ru.java.mentor.oldranger.club.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.FileSystems;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${upload.location}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // актуально для винды
        String absolutePath = FileSystems.getDefault().getPath(uploadDir).normalize().toAbsolutePath().toString().replace('\\', '/');
        String location = "file:///" + absolutePath + "/";
        registry.addResourceHandler("/img/**").
                addResourceLocations(location);
    }
}