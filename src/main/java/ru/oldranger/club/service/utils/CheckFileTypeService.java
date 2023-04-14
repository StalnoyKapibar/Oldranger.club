package ru.oldranger.club.service.utils;

import org.springframework.web.multipart.MultipartFile;

public interface CheckFileTypeService {

    boolean isValidImageFile(MultipartFile image);
}
