package ru.java.mentor.oldranger.club.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.FileSystems;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${upload.location}")
    private String uploadDir;

    @Value("${photoalbums.location}")
    private String mediaDir;

    @Value("${filesInChat.location}")
    private String filesDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // актуально для винды
        String absoluteUploadPath = FileSystems.getDefault().getPath(uploadDir).normalize().toAbsolutePath().toString().replace('\\', '/');
        String uploadlocation = "file:///" + absoluteUploadPath + "/";
        registry.addResourceHandler("/img/**").
                addResourceLocations(uploadlocation);

        String absoluteMediaPath = FileSystems.getDefault().getPath(mediaDir).normalize().toAbsolutePath().toString().replace('\\', '/');
        String absoluteFilePath = FileSystems.getDefault().getPath(filesDir).normalize().toAbsolutePath().toString().replace('\\', '/');
        String filelocation = "file:///" + absoluteFilePath + "/";
        String medialocation = "file:///" + absoluteMediaPath + "/";
        registry.addResourceHandler("/img/chat/**").
                addResourceLocations(medialocation,filelocation);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "OPTIONS", "PUT")
                .allowedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .maxAge(3600);
    }
}