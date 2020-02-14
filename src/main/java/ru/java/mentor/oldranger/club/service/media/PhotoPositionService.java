package ru.java.mentor.oldranger.club.service.media;

import java.util.Optional;

public interface PhotoPositionService {

    Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId);

}
