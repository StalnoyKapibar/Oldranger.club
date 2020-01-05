package ru.java.mentor.oldranger.club.service.media;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import java.util.List;

public interface PhotoService {
    Photo save(PhotoAlbum album, MultipartFile file);

    Photo findById(Long id);

    void deletePhoto(Long id);

    void deletePhotoByName(String name);

    Photo update(Photo photo);

    List<Photo> findOldPhoto(PhotoAlbum album);

    List<Photo> findPhotoByAlbum(PhotoAlbum album);
}
