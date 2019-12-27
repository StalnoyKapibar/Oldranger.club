package ru.java.mentor.oldranger.club.service.media.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.MediaRepository.MediaRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.MediaService;

@Service
public class MediaServiceImpl implements MediaService {
    private MediaRepository repository;
    private static final Logger LOG = LoggerFactory.getLogger(MediaServiceImpl.class);

    @Autowired
    public MediaServiceImpl(MediaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Media save(Media media) {
        LOG.info("Saving media {}", media);
        try {
            media = repository.save(media);
            LOG.info("Media saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return media;
    }

    @Override
    public Media findMediaByUser(User user) {
        LOG.debug("Getting media for user with id = {}", user.getId());
        Media media = null;
        try {
            media = repository.findMediaByUser(user);
            LOG.info("Media returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return media;
    }
}
