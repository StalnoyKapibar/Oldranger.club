package ru.java.mentor.oldranger.club.service.media;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.media.Photo;

public interface PhotoService {
    Photo save(Long albumId, MultipartFile file);

    Photo findById(Long id);

    void deletePhoto(Long id);

    Photo update(Photo photo);
}
