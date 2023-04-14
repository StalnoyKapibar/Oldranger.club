package ru.oldranger.club.service.media;

import java.util.List;
import java.util.Optional;

public interface PhotoPositionService {

    Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId);

    void setPositionPhotoOnAlbumWithIdPhoto(List<Long> positionPhoto, long idAlbum);
}
