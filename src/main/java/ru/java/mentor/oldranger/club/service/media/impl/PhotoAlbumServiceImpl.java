package ru.java.mentor.oldranger.club.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoAlbumRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Service
public class PhotoAlbumServiceImpl implements PhotoAlbumService {
    @Value("${photoalbums.location}")
    private String albumsdDir;

    @Value("${upload.medium}")
    private int medium;
    @Value("${upload.small}")
    private int small;

    private PhotoAlbumRepository albumRepository;

    private UserService userService;

    private PhotoService photoService;

    private MediaService mediaService;

    @Autowired
    public void setMediaService(MediaService service) {
        this.mediaService = service;
    }

    @Autowired
    public void setPhotoService(PhotoService service) {
        this.photoService = service;
    }

    @Autowired
    public void setUserService(UserService service) {
        this.userService = service;
    }

    @Autowired
    public PhotoAlbumServiceImpl(PhotoAlbumRepository repository) {
        this.albumRepository = repository;
    }

    public PhotoAlbumServiceImpl() {
        super();
    }

    @Override
    public PhotoAlbum save(PhotoAlbum album) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByNickName(userName);
        Media media = mediaService.findMediaByUser(user);
        album.setMedia(media);
        PhotoAlbum savedAlbum = albumRepository.save(album);
        File uploadPath = new File(albumsdDir + File.separator + userName + File.separator + "photo_albums" + File.separator + savedAlbum.getId());
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        return savedAlbum;
    }

    @Override
    public List<PhotoAlbum> findAll() {
        return albumRepository.findAll();
    }

    @Override
    @PostConstruct
    public void deleteAllAlbums() {
        File dir = new File(albumsdDir);
        FileSystemUtils.deleteRecursively(dir);
    }

    @Override
    public void deleteAlbum(Long id) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        File dir = new File(albumsdDir + File.separator + userName
                + File.separator + "photo_albums" + File.separator + id);
        FileSystemUtils.deleteRecursively(dir);
        albumRepository.deleteById(id);
    }

    @Override
    public PhotoAlbum findById(Long id) {
        return albumRepository.findById(id).get();
    }

    @Override
    public PhotoAlbum update(PhotoAlbum album) {
        return albumRepository.save(album);
    }

    @Override
    public List<Photo> getAllPhotos(PhotoAlbum album) {
        return photoService.findPhotoByAlbum(album);
    }
}
