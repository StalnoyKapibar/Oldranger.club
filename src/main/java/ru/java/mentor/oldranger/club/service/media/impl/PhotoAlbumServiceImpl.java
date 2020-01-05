package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoAlbumRepository;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoAlbumServiceImpl implements PhotoAlbumService {

    @NonNull
    private PhotoAlbumRepository albumRepository;
    @NonNull
    private UserService userService;
    @NonNull
    private PhotoRepository photoRepository;
    @NonNull
    private MediaService mediaService;

    @Value("${photoalbums.location}")
    private String albumsdDir;

    @Value("${upload.medium}")
    private int medium;
    @Value("${upload.small}")
    private int small;

    @Override
    public PhotoAlbum save(PhotoAlbum album) {
        log.info("Saving album {}", album);
        PhotoAlbum savedAlbum = null;
        try {
            String userName = null;
            if (album.getMedia() == null) {
                userName = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userService.getUserByNickName(userName);
                Media media = mediaService.findMediaByUser(user);
                album.setMedia(media);
            } else {
                userName = album.getMedia().getUser().getNickName();
            }
            savedAlbum = albumRepository.save(album);
            File uploadPath = new File(albumsdDir + File.separator + userName + File.separator + "photo_albums" + File.separator + savedAlbum.getId());
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            log.info("Album saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return savedAlbum;
    }

    @Override
    public List<PhotoAlbum> findAll() {
        log.debug("Getting all albums");
        List<PhotoAlbum> albums = null;
        try {
            albums = albumRepository.findAll();
            log.debug("Returned list of {} albums", albums.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return albums;
    }

    @Override
    @PostConstruct
    public void deleteAllAlbums() {
        log.info("Deleting all albums");
        try {
            File dir = new File(albumsdDir);
            FileSystemUtils.deleteRecursively(dir);
            log.debug("Albums deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteAlbum(Long id) {
        log.info("Deleting album with id = {}", id);
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            File dir = new File(albumsdDir + File.separator + userName
                    + File.separator + "photo_albums" + File.separator + id);
            FileSystemUtils.deleteRecursively(dir);
            albumRepository.deleteById(id);
            log.debug("Album deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public PhotoAlbum findById(Long id) {
        log.debug("Getting album with id = {}", id);
        PhotoAlbum album = null;
        try {
            album = albumRepository.findById(id).get();
            log.debug("Album returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return album;
    }

    @Override
    public PhotoAlbum update(PhotoAlbum album) {
        log.info("Updating album with id = {}", album.getId());
        PhotoAlbum savedAlbum = null;
        try {
            savedAlbum = albumRepository.save(album);
            log.info("Album updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return savedAlbum;
    }

    @Override
    public void deleteAlbumPhotos(boolean deleteAll, PhotoAlbum album) {
        log.debug("Deleting photos from album", album);
        List<Photo> photoList = null;
        if (deleteAll) {
            photoList = photoService.findPhotoByAlbum(album);
        } else {
            photoList = photoService.findOldPhoto(album);
        }
        for (Photo photo : photoList) {
            photoService.findById(photo.getId());
        }
    }

    @Override
    public List<Photo> getAllPhotos(PhotoAlbum album) {
        log.debug("Getting all photos of album {}", album);
        List<Photo> photos = null;
        try {
            photos = photoRepository.findAllByAlbum(album);
            log.debug("Returned list of {} photos", photos.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photos;
    }
}
