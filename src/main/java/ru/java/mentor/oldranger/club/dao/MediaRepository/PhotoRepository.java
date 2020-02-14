package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import java.time.LocalDateTime;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByAlbum(PhotoAlbum album);

    @Query(nativeQuery = true, value = "select * from photos where album_id = 1? and upload_photo_date <= 2?")
    List<Photo> findAllByAlbumAndDate(PhotoAlbum album, LocalDateTime date);

    Photo findByOriginal(String original);

    List<Photo> getAllByAlbumId(long albumId);
}
