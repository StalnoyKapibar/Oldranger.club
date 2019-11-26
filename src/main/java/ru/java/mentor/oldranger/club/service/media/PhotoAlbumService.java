package ru.java.mentor.oldranger.club.service.media;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;

import java.util.List;

public interface PhotoAlbumService {

    void createAlbumDir(String title);

    void deleteAlbumDir(String id);

    PhotoAlbum save(PhotoAlbum album);

    List<PhotoAlbum> findAll();

    void deleteAllAlbums();

    void deleteAlbum(String id);

    PhotoAlbum findByTitle(String title);

    PhotoAlbum findById(Long id);

    Photo addPhotoToDir(MultipartFile file, PhotoAlbum albumId);

    PhotoAlbum update(PhotoAlbum album);
}
