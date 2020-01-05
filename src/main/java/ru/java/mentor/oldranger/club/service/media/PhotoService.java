package ru.java.mentor.oldranger.club.service.media;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.media.Photo;

public interface PhotoService {
    Photo save(PhotoAlbum album, MultipartFile file);

    Photo findById(Long id);

    void deletePhoto(Long id);

    Photo update(Photo photo);

    List<Photo> findOldPhoto(PhotoAlbum album);
}
