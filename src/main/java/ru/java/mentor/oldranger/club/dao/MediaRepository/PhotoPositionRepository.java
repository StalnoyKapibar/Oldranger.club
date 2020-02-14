package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.media.Photo;

import java.util.Optional;


public interface PhotoPositionRepository extends JpaRepository<Photo, Long> {

    @Query(nativeQuery = true, value = "select MAX(position) from photos where album_id = :albumId")
    Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId);
}
