package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoPositionRepository;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoRepository;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.service.media.PhotoPositionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoPositionServiceImpl implements PhotoPositionService {

    private final PhotoPositionRepository photoPositionRepository;
    private final PhotoRepository photoRepository;

    @Override
    public Optional<Long> getMaxPositionOfPhotoOnAlbumWithIdAlbum(long albumId) {
        log.info("Get max position photo on album id = {}", albumId);
        return photoPositionRepository.getMaxPositionOfPhotoOnAlbumWithIdAlbum(albumId);
    }

    @Transactional
    @Override
    public void setPositionPhotoOnAlbumWithIdPhoto(List<Long> positionPhoto, long idAlbum) {
        List<Photo> photos = photoRepository.getAllByAlbumId(idAlbum);
        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPositionPhoto(positionPhoto.get(i));
        }
    }
}
