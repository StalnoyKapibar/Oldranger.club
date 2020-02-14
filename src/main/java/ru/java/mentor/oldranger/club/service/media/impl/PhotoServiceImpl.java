package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoCommentRepository;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoPositionRepository;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoRepository;
import ru.java.mentor.oldranger.club.dto.PhotoCommentDto;
import ru.java.mentor.oldranger.club.model.comment.PhotoComment;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    @NonNull
    private PhotoRepository photoRepository;
    @NonNull
    private PhotoCommentRepository photoCommentRepository;
    @NonNull
    private UserStatisticService userStatisticService;

    private final PhotoPositionRepository photoPositionRepository;


    @Value("${photoalbums.location}")
    private String albumsdDir;

    @Value("${media.medium}")
    private int medium;

    @Value("${media.small}")
    private int small;

    @Override
    //clear cache
    public Photo save(PhotoAlbum album, MultipartFile file, long position) {
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


//            if (!photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(1).isPresent()) {
//                photo.setPositionPhoto(1L);
//            } else {
//                Optional<Long> count = photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(album.getId());
//                long max = (long) count.get();
//                photo.setPositionPhoto(++max);
//            }

//            Optional<Object> position = photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(album.getId());
//            if (position == null) {
//                positionPhotoOnAlbum(photo.getId(), 1, album.getId());
//            } else {
//                positionPhotoOnAlbum(photo.getId(), position, album.getId());
//                photo.setPositionPhoto(position);
//            }
            photo.setPositionPhoto(position);

            photo.setUploadPhotoDate(LocalDateTime.now());

            photo = photoRepository.save(photo);

            log.debug("Photo saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return photo;
    }

    @Override
    //add caching
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
    //add caching
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
    public PhotoComment getCommentById(Long id) {
        Optional<PhotoComment> comment = photoCommentRepository.findById(id);
        return comment.orElseThrow(() -> new RuntimeException("Not found comment by id: " + id));
    }

    @Override
    public void deleteComment(long id) {
        photoCommentRepository.deleteById(id);
    }

    @Override
    public void addCommentToPhoto(PhotoComment photoComment) {
        Photo photo = photoComment.getPhoto();
        long comments = photo.getCommentCount();
        photoComment.setPosition(++comments);
        photo.setCommentCount(comments);
        photoCommentRepository.save(photoComment);
        UserStatistic userStatistic = userStatisticService.getUserStaticByUser(photoComment.getUser());
        comments = userStatistic.getMessageCount();
        userStatistic.setMessageCount(++comments);
        userStatistic.setLastComment(photoComment.getDateTime());
        userStatisticService.saveUserStatic(userStatistic);
    }

    @Override
    public void updatePhotoComment(PhotoComment photoComment) {
        photoCommentRepository.save(photoComment);
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
            if (list.size() != 0) {
                dtoList = list.subList(
                        Math.min(position, list.size() - 1),
                        Math.min(position + pageable.getPageSize(), list.size()))
                        .stream().map(this::assembleCommentDto).collect(Collectors.toList());
            } else {
                dtoList = Collections.emptyList();
            }
            page = new PageImpl<PhotoCommentDto>(dtoList, pageable, dtoList.size());
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
    //clear cache
    public void deletePhotoByName(String name) {
        try {
            log.debug("Getting photo by name {} to delete", name);
            Photo photo = photoRepository.findByOriginal(name);
            deletePhoto(photo.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    //clear cache
    public void deletePhoto(Long id) {
        log.info("Deleting photo with id = {}", id);
        try {
            Photo photo = findById(id);
            File file = new File(albumsdDir + File.separator + photo.getOriginal());
            FileSystemUtils.deleteRecursively(file);

            file = new File(albumsdDir + File.separator + photo.getSmall());
            FileSystemUtils.deleteRecursively(file);

            photoRepository.delete(photo);
            log.debug("Photo deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    //clear cache
    public Photo update(MultipartFile newPhoto, Photo photo) {
        log.info("Updating photo with id = {}", photo.getId());
        Photo updatedPhoto = null;
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            String pathToImg = userName + File.separator + "photo_albums" + File.separator + photo.getAlbum().getId() + File.separator;
            String fileName = UUID.randomUUID().toString() + StringUtils.cleanPath(newPhoto.getOriginalFilename());
            String resultFileName = pathToImg + fileName;

            File uploadPath = new File(albumsdDir + File.separator + resultFileName);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path copyLocation = Paths.get(uploadPath + File.separator + fileName);

            Files.copy(newPhoto.getInputStream(), copyLocation);


            Thumbnails.of(uploadPath + File.separator + fileName)
                    .size(small, small)
                    .toFile(uploadPath + File.separator + "small_" + fileName);

            photo.setOriginal(resultFileName + File.separator + fileName);
            photo.setSmall(resultFileName + File.separator + fileName);
            photo.setUploadPhotoDate(LocalDateTime.now());
            photo = photoRepository.save(photo);
            log.debug("Photo updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return updatedPhoto;
    }

    @Transactional
    @Override
    public void positionPhotoOnAlbum(long photoId, long position, long albumId) {
        log.info("Change position of photo on album with id = {}", photoId);
        if (albumId != 0) {
            if (photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(albumId).isPresent()) {
                photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, 1);
            } else {
                Optional<Long> count = photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(albumId);
                long g = (long) count.get();
                photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, ++g);
            }
        } else {
            photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, 0);
        }
        log.debug("Changed position");
    }
}
