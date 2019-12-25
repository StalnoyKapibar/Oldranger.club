package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.MediaRepository.MediaRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.MediaService;

@Service
@AllArgsConstructor
public class MediaServiceImpl implements MediaService {
    private MediaRepository repository;

    @Override
    public Media save(Media media) {
        return repository.save(media);
    }

    @Override
    public Media findMediaByUser(User user) {
        return repository.findMediaByUser(user);
    }


}
