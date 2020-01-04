package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    @NonNull
    private PhotoRepository photoRepository;

    @Value("${photoalbums.location}")
    private String albumsdDir;

    @Value("${media.medium}")
    private int medium;

    @Value("${media.small}")
    private int small;

    @Override
    public Photo save(PhotoAlbum album, MultipartFile file) {
        log.info("Saving photo to album with id = {}", album.getId());
        Photo photo = null;
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            String pathToImg = userName + File.separator + "photo_albums" + File.separator + album.getId() + File.separator;
            String fileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
            String resultFileName = pathToImg + fileName;

            File uploadPath = new File(albumsdDir + File.separator + resultFileName);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path copyLocation = Paths.get(uploadPath + File.separator + fileName);

            Files.copy(file.getInputStream(), copyLocation);


            Thumbnails.of(uploadPath + File.separator + fileName)
                    .size(small, small)
                    .toFile(uploadPath + File.separator + "small_" + fileName);

            photo = new Photo(resultFileName + File.separator + fileName,
                    resultFileName + File.separator + "small_" + fileName);
            photo.setAlbum(album);
            photo.setUploadPhotoDate(LocalDateTime.now());

            photo = photoRepository.save(photo);

            log.debug("Photo saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    public Photo findById(Long id) {
        log.debug("Getting photo with id = {}", id);
        Photo photo = null;
        try {
            photo = photoRepository.findById(id).get();
            log.debug("Album returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    public List<Photo> findPhotoByAlbum(PhotoAlbum album) {
        log.debug("Getting photos of album {}", album);
        List<Photo> photos = null;
        try {
            photos = photoRepository.findAllByAlbum(album);
            log.debug("Returned list of {} photos", photos.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photos;
    }

    @Override
    public List<Photo> findOldPhoto(PhotoAlbum album) {
        return photoRepository.findAllByAlbumAndDate(album, LocalDateTime.now().minusMinutes(1L));
    }

    @Override
    public void deletePhoto(Long id) {
        log.info("Deleting photo with id = {}", id);
        try {
            Photo photo = findById(id);
            PhotoAlbum album = photo.getAlbum();
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            File file = new File(albumsdDir + File.separator + userName
                    + File.separator + "photo_albums" + File.separator + album.getId() + File.separator + photo.getOriginal());
            FileSystemUtils.deleteRecursively(file);
            photoRepository.delete(photo);
            log.debug("Photo deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public Photo update(Photo photo) {
        log.info("Updating photo with id = {}", photo.getId());
        Photo updatedPhoto = null;
        try {
            updatedPhoto = photoRepository.save(photo);
            log.debug("Photo saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updatedPhoto;
    }
}
