package ru.java.mentor.oldranger.club.service.media;

import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;

import java.util.List;

public interface PhotoAlbumService {

    PhotoAlbum createAlbum(String title);

    void save(PhotoAlbum album);

    List<PhotoAlbum> findAll();

    List<Photo> findAllPhotos(String title);

    void renameAlbum(String oldTitle, String newTitle);

    void deleteAlbum(String title);

    PhotoAlbum findByTitle(String title);

    PhotoAlbum findById(Long id);
}
