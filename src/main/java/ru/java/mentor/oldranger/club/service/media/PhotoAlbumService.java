package ru.java.mentor.oldranger.club.service.media;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import java.util.List;

public interface PhotoAlbumService {

    void createAlbumDir(String title);

    void deleteAlbumDir(Long id);

    PhotoAlbum save(PhotoAlbum album);

    List<PhotoAlbum> findAll();

    void deleteAllAlbums();

    void deleteAlbum(Long id);

    PhotoAlbum findById(Long id);

    Photo addPhotoToDir(MultipartFile file, PhotoAlbum albumId);

    PhotoAlbum update(PhotoAlbum album);

    void deletePhotoFromDir(PhotoAlbum album, Photo photo);
}
