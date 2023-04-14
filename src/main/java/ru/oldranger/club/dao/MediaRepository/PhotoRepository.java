package ru.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oldranger.club.dto.PhotoDTO;
import ru.oldranger.club.model.media.Photo;
import ru.oldranger.club.model.media.PhotoAlbum;

import java.time.LocalDateTime;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByAlbum(PhotoAlbum album);

    @Query(value = "select new ru.oldranger.club.dto.PhotoDTO" +
            "(p.id" +
            ",p.description, p.uploadPhotoDate, p.commentCount" +
            ") " +
            "from Photo p where p.album=:album")
    List<PhotoDTO> findPhotoDTOByAlbum(PhotoAlbum album);

    @Query(nativeQuery = true, value = "select * from photos where album_id = ?1 and upload_photo_date <= ?2")
    List<Photo> findAllByAlbumAndDate(PhotoAlbum album, LocalDateTime date);

    Photo findByOriginal(String original);

    @Query(nativeQuery = true, value = "select * from photos where album_id in (" +
            "select id from photo_album where title = ?1" +
            ") and description = ?2")
    List<Photo> findByAlbumTitleAndDescription(String albumTitle, String description);

    List<Photo> getAllByAlbumId(long albumId);
}
