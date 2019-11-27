package ru.java.mentor.oldranger.club.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.media.MediaRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.service.media.MediaService;

@Service
public class MediaServiceImpl implements MediaService {
    private MediaRepository repository;

    @Autowired
    public MediaServiceImpl(MediaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Media save(Media media) {
        return repository.save(media);
    }
}
