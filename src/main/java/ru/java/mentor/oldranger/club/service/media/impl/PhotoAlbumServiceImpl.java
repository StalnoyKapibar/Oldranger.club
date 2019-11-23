package ru.java.mentor.oldranger.club.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import ru.java.mentor.oldranger.club.dao.UserRepository.media.PhotoAlbumRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.media.Media;
import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Service
public class PhotoAlbumServiceImpl implements PhotoAlbumService {
    @Value("${photoalbums.location}")
    private String albumsdDir;

    private PhotoAlbumRepository albumRepository;

    private UserService userService;

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
    public void createAlbum(String title) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        File uploadPath = new File(albumsdDir + File.separator + userName + File.separator + "photo_albums" + File.separator + title);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
    }

    @Override
    public PhotoAlbum save(PhotoAlbum album) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByNickName(userName);
        Media media = user.getMedia();
        media.getPhotoAlbums().add(album);
        return albumRepository.save(album);

    }

    @Override

    public List<PhotoAlbum> findAll() {
        return albumRepository.findAll();
    }

    @Override
    @PostConstruct
    public void deleteAllAlbums() {
        File dir = new File(albumsdDir);
        boolean result = FileSystemUtils.deleteRecursively(dir);
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
