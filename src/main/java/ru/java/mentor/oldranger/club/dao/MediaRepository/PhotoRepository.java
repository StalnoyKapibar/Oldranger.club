package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.PhotoDTO;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import java.time.LocalDateTime;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByAlbum(PhotoAlbum album);

    @Query(value = "select new ru.java.mentor.oldranger.club.dto.PhotoDTO" +
            "(p.id" +
            ",p.description, p.uploadPhotoDate, p.commentCount" +
            ") " +
            "from Photo p where p.album=:album")
    List<PhotoDTO> findPhotoDTOByAlbum(PhotoAlbum album);

    @Query(nativeQuery = true, value = "select * from photos where album_id = ?1 and upload_photo_date <= ?2")
    List<Photo> findAllByAlbumAndDate(PhotoAlbum album, LocalDateTime date);

    Photo findByOriginal(String original);


    @Query(nativeQuery = true, value = "SELECT p.* FROM photos p " +
            "JOIN photo_album a " +
            "ON a.id = p.album_id " +
            "WHERE " +
            "a.title = ?1 AND p.description = ?2 ORDER BY p.id")
    List<Photo> findByAlbumTitleAndDescription(String albumTitle, String description);

    List<Photo> getAllByAlbumId(long albumId);
}
