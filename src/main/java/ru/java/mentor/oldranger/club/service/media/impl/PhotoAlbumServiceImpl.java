package ru.java.mentor.oldranger.club.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.media.PhotoAlbumRepository;
import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;

import java.util.List;

@Service
public class PhotoAlbumServiceImpl implements PhotoAlbumService {
    @Value("${upload.location:${user.home}}")
    private String uploadDir;


    private PhotoAlbumRepository albumRepository;

    @Autowired
    public PhotoAlbumServiceImpl(PhotoAlbumRepository repository) {
        this.albumRepository = repository;
    }

    public PhotoAlbumServiceImpl() {
        super();
    }

    @Override
    public PhotoAlbum createAlbum(String title) {
        PhotoAlbum album = new PhotoAlbum(title);
        return album;
    }

    @Override
    public void save(PhotoAlbum album) {
        albumRepository.save(album);
    }

    @Override
    public List<PhotoAlbum> findAll() {
        return null;
    }

    @Override
    public List<Photo> findAllPhotos(String title) {
        return null;
    }

    @Override
    public void renameAlbum(String oldTitle, String newTitle) {

    }

    @Override
    public void deleteAlbum(String title) {

    }

    @Override
    public PhotoAlbum findByTitle(String title) {
        return null;
    }

    @Override
    public PhotoAlbum findById(Long id) {
        return null;
    }
}
