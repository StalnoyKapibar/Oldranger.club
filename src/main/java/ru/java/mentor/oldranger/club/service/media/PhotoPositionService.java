package ru.java.mentor.oldranger.club.service.media;

import java.util.Optional;

public interface PhotoPositionService {

    Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId);

    void setPositionPhotoOnAlbum(long idPhoto, long position, long idAlbum);

    void setPositionPhotoOnAlbumWithAlbumIdAndPosition(long positionNew, long positionOld, long albumId);

    Long getPositionOfPhotoOnAlbumWithIdPhoto(long photoId);
}
