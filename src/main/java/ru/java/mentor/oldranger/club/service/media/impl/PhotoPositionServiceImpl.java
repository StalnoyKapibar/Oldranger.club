package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoPositionRepository;
import ru.java.mentor.oldranger.club.service.media.PhotoPositionService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoPositionServiceImpl implements PhotoPositionService {

    private final PhotoPositionRepository photoPositionRepository;

    @Override
    public Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId) {
        log.info("Get max position photo on album id = {}", albumId);
        return photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(albumId);
    }

    @Transactional
    @Override
    //массив айдишников
    public void setPositionPhotoOnAlbum(long photoId, long position, long albumId) {
//        log.info("Change position of photo on album with id = {}", photoId);
//        if (albumId != 0) {
//            if (photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(albumId).isPresent()) {
//                photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, 1);
//            } else {
//                Optional<Long> count = photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(albumId);
//                long g = (long) count.get();
//                photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, ++g);
//            }
//        } else {
//            photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, 0);
//        }
//        log.debug("Changed position");
        log.info("Change position of photo on album with id = {}", photoId);

        long positionOld = photoPositionRepository.getPositionOfPhotoOnAlbumWithIdPhoto(photoId);

        long difference = positionOld - position;
        if (positionOld < position) {
            for (int i = 0; i < Math.abs(difference); i++) {
               long positionn = --position;
               long positiono = position--;

                photoPositionRepository.setPositionPhotoOnAlbumWithPositionPhoto(positionn, positiono, albumId);
            }
            photoPositionRepository.setPositionPhotoOnAlbumWithId(photoId, 4);
        } else {
            for (int i = 0; i < Math.abs(difference); i++) {
                long positionn = --position;
                long positiono = position--;
                photoPositionRepository.setPositionPhotoOnAlbumWithPositionPhoto(positionn, positiono, albumId);
            }
        }
        log.debug("Changed position");
    }

    @Transactional
    @Override
    public void setPositionPhotoOnAlbumWithAlbumIdAndPosition(long positionNew, long positionOld, long albumId) {
        log.info("Change position of photo on album with old position = {} on new position = {} in album = {}", positionOld, positionNew, albumId);
        photoPositionRepository.setPositionPhotoOnAlbumWithPositionPhoto(positionNew, positionOld, albumId);
    }

    @Override
    public Long getPositionOfPhotoOnAlbumWithIdPhoto(long photoId) {
        return photoPositionRepository.getPositionOfPhotoOnAlbumWithIdPhoto(photoId);
    }
}
