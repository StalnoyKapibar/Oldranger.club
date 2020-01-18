package ru.java.mentor.oldranger.club.service.utils.impl;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.service.utils.CheckFileTypeService;


@Service
public class CheckFileTypeServiceImpl implements CheckFileTypeService {
    @Override
    public boolean isValidImageFile(@Nullable MultipartFile image) {
        String imageContentType = null;
        if (image != null) {
            imageContentType = image.getContentType();
        }
        if (image == null) {
            return true;
        } else {
            return imageContentType.contains("jpeg") || imageContentType.contains("png");
        }
    }
}