package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import java.time.LocalDateTime;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByAlbum(PhotoAlbum album);

    @Query(nativeQuery = true, value = "select * from photos where album_id = ?1 and upload_photo_date <= ?2")
    List<Photo> findAllByAlbumAndDate(PhotoAlbum album, LocalDateTime date);

    Photo findByOriginal(String original);

    @Query(nativeQuery = true, value = "select * from photos where album_id in (" +
            "select id from photo_album where title = ?1" +
            ") and description = ?2")
    List<Photo> findByAlbumTitleAndDescription(String albumTitle, String description);
}
