package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.MediaRepository.MediaRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.MediaService;

@Slf4j
@Service
@AllArgsConstructor
public class MediaServiceImpl implements MediaService {
    private MediaRepository repository;

    @Override
    public Media save(Media media) {
        log.info("Saving media {}", media);
        try {
            media = repository.save(media);
            log.info("Media saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return media;
    }

    @Override
    public Media findMediaByUser(User user) {
        log.debug("Getting media for user with id = {}", user.getId());
        Media media = null;
        try {
            media = repository.findMediaByUser(user);
            log.info("Media returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return media;
    }
}
