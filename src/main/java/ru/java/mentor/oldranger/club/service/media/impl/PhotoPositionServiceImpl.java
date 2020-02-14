package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
}
