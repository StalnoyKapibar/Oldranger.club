package ru.java.mentor.oldranger.club.service.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadImageService {
    void uploadImage(MultipartFile file) throws IOException;
}
