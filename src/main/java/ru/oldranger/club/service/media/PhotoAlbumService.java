package ru.oldranger.club.service.media;

import ru.oldranger.club.dto.PhotoAlbumDto;
import ru.oldranger.club.dto.PhotoWithAlbumDTO;
import ru.oldranger.club.model.media.Photo;
import ru.oldranger.club.model.media.PhotoAlbum;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface PhotoAlbumService {

    PhotoAlbum save(PhotoAlbum album);

    List<PhotoAlbum> findAll();

    List<PhotoWithAlbumDTO> getAllPhotoWithAlbumsDTO(PhotoAlbum album);

    List<Photo> getAllPhotos(PhotoAlbum album);

    void deleteAllAlbums();

    void deleteAlbum(Long id);

    PhotoAlbum findById(Long id);

    PhotoAlbum update(PhotoAlbum album);

    void deleteAlbumPhotos(boolean deleteAll, PhotoAlbum album);

    List<PhotoAlbum> findPhotoAlbumsViewedByUser(User user);

    List<PhotoAlbumDto> findPhotoAlbumsDtoOwnedByUser(User user);

    PhotoAlbum findPhotoAlbumByTitle(String name);

    void createAlbum(PhotoAlbum photoAlbum);

    PhotoAlbumDto assemblePhotoAlbumDto(PhotoAlbum album);
}
