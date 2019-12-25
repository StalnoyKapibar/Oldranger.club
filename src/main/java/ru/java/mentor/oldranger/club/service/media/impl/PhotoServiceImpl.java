package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoRepository;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private PhotoRepository photoRepository;

    private PhotoAlbumService albumService;

    @Value("${photoalbums.location}")
    private String albumsdDir;

    @Value("${upload.medium}")
    private int medium;

    @Value("${upload.small}")
    private int small;

    @Override
    public Photo save(Long albumId, MultipartFile file) {
        PhotoAlbum album = albumService.findById(albumId);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String resultFileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
        File uploadPath = new File(albumsdDir + File.separator + userName
                + File.separator + "photo_albums" + File.separator + album.getId() + File.separator + resultFileName);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        Path copyLocation = Paths.get(uploadPath + File.separator + resultFileName);
        try {
            Files.copy(file.getInputStream(), copyLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thumbnails.of(uploadPath + File.separator + resultFileName)
                    .size(medium, medium)
                    .toFile(uploadPath + File.separator + "small_" + resultFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Photo photo = new Photo(resultFileName, "small_" + resultFileName);
        photo.setAlbum(album);
        photo = photoRepository.save(photo);
        albumService.update(album);
        return photo;
    }

    @Override
    public Photo findById(Long id) {
        return photoRepository.findById(id).get();
    }

    @Override
    public List<Photo> findPhotoByAlbum(PhotoAlbum album) {
        return photoRepository.findAllByAlbum(album);
    }

    @Override
    public void deletePhoto(Long id) {
        Photo photo = findById(id);
        PhotoAlbum album = photo.getAlbum();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        File file = new File(albumsdDir + File.separator + userName
                + File.separator + "photo_albums" + File.separator + album.getId() + File.separator + photo.getOriginal());
        FileSystemUtils.deleteRecursively(file);
        photoRepository.delete(photo);
    }

    @Override
    public Photo update(Photo photo) {
        return photoRepository.save(photo);
    }
}
