package ru.oldranger.club.service.media;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.oldranger.club.dto.PhotoCommentDto;
import ru.oldranger.club.dto.PhotoDTO;
import ru.oldranger.club.model.comment.PhotoComment;
import ru.oldranger.club.model.media.Photo;
import ru.oldranger.club.model.media.PhotoAlbum;

import java.util.List;

public interface PhotoService {
    Photo save(PhotoAlbum album, MultipartFile file, long position);

    Photo save(PhotoAlbum album, MultipartFile file, String description);

    Photo findById(Long id);

    void deletePhoto(Long id);

    void deletePhotoByName(String name);

    Photo update(MultipartFile newPhoto, Photo photo);

    List<Photo> findOldPhoto(PhotoAlbum album);

    List<Photo> findPhotoByAlbum(PhotoAlbum album);

    List<PhotoDTO> findPhotoDTOByAlbum(PhotoAlbum album);

    PhotoComment getCommentById(Long id);

    void deleteComment(long id);

    void addCommentToPhoto(PhotoComment photoComment);

    void updatePhotoComment(PhotoComment photoComment);

    Page<PhotoCommentDto> getPageableCommentDtoByPhoto(Photo photo, Pageable pageable, int position);

    PhotoCommentDto assembleCommentDto(PhotoComment comment);

    List<Photo> findByAlbumTitleAndDescription(String albumTitle, String description);
}
