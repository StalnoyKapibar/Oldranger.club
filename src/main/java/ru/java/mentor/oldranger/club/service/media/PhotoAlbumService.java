package ru.java.mentor.oldranger.club.service.media;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;

import java.util.List;

public interface PhotoAlbumService {

    void createAlbum(String title);

    PhotoAlbum save(PhotoAlbum album);

    List<PhotoAlbum> findAll();

    List<Photo> findAllPhotos(String title);

    void renameAlbum(String oldTitle, String newTitle);

    void deleteAlbum(String title);

    void deleteAllAlbums();

    PhotoAlbum findByTitle(String title);

    PhotoAlbum findById(Long id);

    Photo addPhotoToDir(MultipartFile file, PhotoAlbum albumId);

    PhotoAlbum update(PhotoAlbum album);
}
