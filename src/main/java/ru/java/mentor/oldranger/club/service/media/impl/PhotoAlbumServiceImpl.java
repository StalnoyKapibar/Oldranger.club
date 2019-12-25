package ru.java.mentor.oldranger.club.service.media.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(PhotoAlbumServiceImpl.class);

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
        LOG.info("Saving album {}", album);
        PhotoAlbum savedAlbum = null;
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByNickName(userName);
            Media media = mediaService.findMediaByUser(user);
            album.setMedia(media);
            savedAlbum = albumRepository.save(album);
            File uploadPath = new File(albumsdDir + File.separator + userName + File.separator + "photo_albums" + File.separator + savedAlbum.getId());
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            LOG.info("Album saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return savedAlbum;
    }

    @Override
    public List<PhotoAlbum> findAll() {
        LOG.debug("Getting all albums");
        List<PhotoAlbum> albums = null;
        try {
            albums = albumRepository.findAll();
            LOG.debug("Returned list of {} albums", albums.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return albums;
    }

    @Override
    @PostConstruct
    public void deleteAllAlbums() {
        LOG.info("Deleting all albums");
        try {
            File dir = new File(albumsdDir);
            FileSystemUtils.deleteRecursively(dir);
            LOG.debug("Albums deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteAlbum(Long id) {
        LOG.info("Deleting album with id = {}", id);
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            File dir = new File(albumsdDir + File.separator + userName
                    + File.separator + "photo_albums" + File.separator + id);
            FileSystemUtils.deleteRecursively(dir);
            albumRepository.deleteById(id);
            LOG.debug("Album deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public PhotoAlbum findById(Long id) {
        LOG.debug("Getting album with id = {}", id);
        PhotoAlbum album = null;
        try {
            album = albumRepository.findById(id).get();
            LOG.debug("Album returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return album;
    }

    @Override
    public PhotoAlbum update(PhotoAlbum album) {
        LOG.info("Updating album with id = {}", album.getId());
        PhotoAlbum savedAlbum = null;
        try {
            savedAlbum = albumRepository.save(album);
            LOG.info("Album updated");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return savedAlbum;
    }

    @Override
    public List<Photo> getAllPhotos(PhotoAlbum album) {
        LOG.debug("Getting all photos of album {}", album);
        List<Photo> photos = null;
        try {
            photos = photoService.findPhotoByAlbum(album);
            LOG.debug("Returned list of {} photos", photos.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return photos;
    }
}
