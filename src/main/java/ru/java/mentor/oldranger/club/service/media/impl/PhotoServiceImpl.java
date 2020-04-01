package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoAlbumRepository;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoCommentRepository;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoRepository;
import ru.java.mentor.oldranger.club.dto.PhotoCommentDto;
import ru.java.mentor.oldranger.club.dto.PhotoDTO;
import ru.java.mentor.oldranger.club.model.comment.PhotoComment;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"photo"}, cacheManager = "generalCacheManager")
public class PhotoServiceImpl implements PhotoService {
    @NonNull
    private PhotoAlbumRepository photoAlbumRepository;
    @NonNull
    private PhotoRepository photoRepository;
    @NonNull
    private PhotoCommentRepository photoCommentRepository;
    @NonNull
    private UserStatisticService userStatisticService;

    @Value("${photoalbums.location}")
    private String albumsDir;
    @Value("${media.medium}")
    private int medium;
    @Value("${media.small}")
    private int small;

    private static final String SIZE_PHOTO = "small_";

    @Override
    public Photo save(PhotoAlbum album, MultipartFile file, String description) {
        Photo photo = save(album, file, 0);
        photo.setDescription(description);

        if (album.getThumbImage() == null) {
            album.setThumbImage(photo);
            photoAlbumRepository.save(album);
        }

        return photoRepository.save(photo);
    }

    @Override
    public Photo save(PhotoAlbum album, MultipartFile file, long position) {
        log.info("Saving photo to album with id = {}", album.getId());
        Photo photo = null;
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            String pathToImg = userName + File.separator + "photo_albums" + File.separator + album.getId() + File.separator;
            String fileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
            String resultFileName = pathToImg + fileName;

            File uploadPath = new File(albumsDir + File.separator + resultFileName);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path copyLocation = Paths.get(uploadPath + File.separator + fileName);

            Files.copy(file.getInputStream(), copyLocation);

            Thumbnails.of(uploadPath + File.separator + fileName)
                    .size(small, small)
                    .toFile(uploadPath + File.separator + SIZE_PHOTO + fileName);

            photo = new Photo(resultFileName + File.separator + fileName,
                    resultFileName + File.separator + SIZE_PHOTO + fileName);
            photo.setAlbum(album);

            photo.setPositionPhoto(position);

            photo.setUploadPhotoDate(LocalDateTime.now());

            photo = photoRepository.save(photo);

            if (album.getThumbImage() == null) {
                album.setThumbImage(photo);
                photoAlbumRepository.save(album);
            }

            log.debug("Photo saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    @Cacheable
    public Photo findById(Long id) {
        log.debug("Getting photo with id = {}", id);
        Photo photo = null;
        try {
            photo = photoRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Not found photo with id = " + id));
            log.debug("Photo returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    public List<PhotoDTO> findPhotoDTOByAlbum(PhotoAlbum album) {
        log.debug("Getting photos of album {}", album);
        List<PhotoDTO> photos = null;
        try {
            photos = photoRepository.findPhotoDTOByAlbum(album);
            log.debug("Returned list of {} photos", photos.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photos;
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
    @Cacheable(value = "photoComment")
    public PhotoComment getCommentById(Long id) {
        log.debug("Getting comment to photo with id = {}", id);
        PhotoComment comment = null;
        try {
            comment = photoCommentRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Not found comment by id: " + id));
            log.debug("Comment returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return comment;
    }

    //TODO Когда удаляется комментарий, позиции других комментариев должны изменяться?
    @Override
    @Caching(evict = {
            @CacheEvict(key = "#result.photo.id"),
            @CacheEvict(value = "photoComment")})
    public PhotoComment deleteComment(long id) {
        log.info("Deleting comment to photo with id: {}", id);
        PhotoComment comment = getCommentById(id);
        Photo photo = comment.getPhoto();
        photo.setCommentCount(photo.getCommentCount() - 1);
        photoRepository.save(photo);
        photoCommentRepository.deleteById(id);
        log.info("Comment deleted");
        return comment;
    }

    @Override
    @CacheEvict(key = "#result.photo.id")
    public PhotoComment addCommentToPhoto(PhotoComment photoComment) {
        Photo photo = photoComment.getPhoto();
        Long comments = photo.getCommentCount();
        if (comments == null) {
            comments = 0L;
        }
        photoComment.setPosition(++comments);
        photo.setCommentCount(comments);
        photoRepository.save(photo);
        PhotoComment savedComment = photoCommentRepository.save(photoComment);
        UserStatistic userStatistic = userStatisticService.getUserStaticByUser(photoComment.getUser());
        comments = userStatistic.getMessageCount();
        userStatistic.setMessageCount(++comments);
        userStatistic.setLastComment(photoComment.getDateTime());
        userStatisticService.saveUserStatic(userStatistic);
        return savedComment;
    }

    @Override
    @CachePut(value = "photoComment", key = "#photoComment.id")
    public PhotoComment updatePhotoComment(PhotoComment photoComment) {
        return photoCommentRepository.save(photoComment);
    }

    @Override
    public Page<PhotoCommentDto> getPageableCommentDtoByPhoto(Photo photo, Pageable pageable, int position) {
        log.debug("Getting page {} of comments dto for photo with id = {}", pageable.getPageNumber(), photo.getId());
        Page<PhotoCommentDto> page = null;
        List<PhotoComment> list = new ArrayList<>();
        try {
            photoCommentRepository.findByPhoto(photo,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() + position, pageable.getSort()))
                    .map(list::add);
            List<PhotoCommentDto> dtoList = null;
            if (!list.isEmpty()) {
                dtoList = list.subList(
                        Math.min(position, list.size() - 1),
                        Math.min(position + pageable.getPageSize(), list.size()))
                        .stream().map(this::assembleCommentDto).collect(Collectors.toList());
            } else {
                dtoList = Collections.emptyList();
            }
            page = new PageImpl<>(dtoList, pageable, dtoList.size());
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public PhotoCommentDto assembleCommentDto(PhotoComment comment) {
        log.debug("Assembling photo comment {} dto", comment);
        PhotoCommentDto commentDto = new PhotoCommentDto();
        try {
            commentDto.setPositionInPhoto(comment.getPosition());
            commentDto.setPhotoId(comment.getPhoto().getId());
            commentDto.setAuthor(comment.getUser());
            commentDto.setCommentDateTime(comment.getDateTime());
            commentDto.setCommentCount(comment.getPhoto().getCommentCount());
            commentDto.setCommentText(comment.getCommentText());
            log.debug("Comment dto assembled");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return commentDto;
    }

    @Override
    public List<Photo> findOldPhoto(PhotoAlbum album) {
        return photoRepository.findAllByAlbumAndDate(album, LocalDateTime.now().minusMinutes(1L));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#result.id"),
            @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#result.id+'original'"),
            @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#result.id+'small'")})
    public Photo deletePhotoByName(String name) {
        Photo photo = null;
        try {
            log.debug("Getting photo by name {} to delete", name);
            photo = photoRepository.findByOriginal(name);
            deletePhoto(photo.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    @Caching(evict = {
            @CacheEvict,
            @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#id+'original'"),
            @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#id+'small'"),
            @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager")})
    public void deletePhoto(Long id) {
        log.info("Deleting photo with id = {}", id);
        try {
            Photo photo = findById(id);
            PhotoAlbum photoAlbum = photo.getAlbum();
            List<Photo> photoList = photoRepository.findAllByAlbum(photoAlbum);
            if (photoAlbum.getThumbImage() != null && id.equals(photoAlbum.getThumbImage().getId())) {
                for (Photo photo1 : photoList) {
                    if (!photo1.getId().equals(id)) {
                        photoAlbum.setThumbImage(photo1);
                        break;
                    }
                }
                if (photoAlbum.getThumbImage().getId().equals(id)) {
                    photoAlbum.setThumbImage(null);
                }
            }
            photoAlbumRepository.save(photoAlbum);
            log.debug("PhotoAlbum save");

            File file = new File(albumsDir + File.separator + photo.getOriginal());
            FileSystemUtils.deleteRecursively(file);

            file = new File(albumsDir + File.separator + photo.getSmall());
            FileSystemUtils.deleteRecursively(file);

            photoRepository.delete(photo);

            log.debug("Photo deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Caching(
            put = {
                    @CachePut(key = "#photo.id")
            },
            evict = {
                    @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#photo.id+'original'"),
                    @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#photo.id+'small'"),
                    @CacheEvict(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#photo.id")})
    public Photo update(MultipartFile newPhoto, Photo photo) {
        log.info("Updating photo with id = {}", photo.getId());
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            String pathToImg = userName + File.separator + "photo_albums" + File.separator + photo.getAlbum().getId() + File.separator;
            String fileName = UUID.randomUUID().toString() + StringUtils.cleanPath(newPhoto.getOriginalFilename());
            String resultFileName = pathToImg + fileName;

            File uploadPath = new File(albumsDir + File.separator + resultFileName);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path copyLocation = Paths.get(uploadPath + File.separator + fileName);

            Files.copy(newPhoto.getInputStream(), copyLocation);


            Thumbnails.of(uploadPath + File.separator + fileName)
                    .size(small, small)
                    .toFile(uploadPath + File.separator + SIZE_PHOTO + fileName);

            photo.setOriginal(resultFileName + File.separator + fileName);
            photo.setSmall(resultFileName + File.separator + fileName);
            photo.setUploadPhotoDate(LocalDateTime.now());
            photoRepository.save(photo);

            log.debug("Photo updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    public List<Photo> findByAlbumTitleAndDescription(String albumTitle, String description) {
        return photoRepository.findByAlbumTitleAndDescription(albumTitle, description);
    }

    @Override
    @Caching(cacheable = {
            @Cacheable(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#photo.id+#type", condition = "#type!=null"),
            @Cacheable(value = "photoFile", cacheManager = "mediaFileCacheManager", key = "#photo.id", condition = "#type==null")})
    public byte[] getPhotoAsByteArray(Photo photo, String type) throws IOException {
        log.info("Before method.getPhotoAsByteArray(Photo photo, String type) in PhotoServiceImp");
        return IOUtils.toByteArray(new FileInputStream(
                new File(albumsDir + File.separator +
                        (type == null || type.equals("original") ? photo.getOriginal() : photo.getSmall()))));
    }

}
