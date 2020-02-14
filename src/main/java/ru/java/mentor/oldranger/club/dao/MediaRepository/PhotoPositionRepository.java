package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.media.Photo;

import java.util.Optional;


public interface PhotoPositionRepository extends JpaRepository<Photo, Long> {

    @Query(nativeQuery = true, value = "select MAX(position) from photos where album_id = :albumId")
    Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId);

    @Modifying
    @Query(nativeQuery = true, value = "update photos set position = :position where id = :photoId")
    void setPositionPhotoOnAlbumWithId(long photoId, long position);

    @Modifying
    @Query(nativeQuery = true, value = "update photos set position = :positionNew where album_id = :albumId and position = :positionOld")
    void setPositionPhotoOnAlbumWithPositionPhoto(long positionNew, long positionOld, long albumId);

    @Query(nativeQuery = true, value = "select position from photos where id = :photoId")
    Long getPositionOfPhotoOnAlbumWithIdPhoto(long photoId);
}
