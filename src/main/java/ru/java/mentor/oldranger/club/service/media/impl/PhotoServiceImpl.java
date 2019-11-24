package ru.java.mentor.oldranger.club.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.media.PhotoRepository;
import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

@Service
public class PhotoServiceImpl implements PhotoService {
    private PhotoRepository photoRepository;

    @Autowired
    public PhotoServiceImpl(PhotoRepository repository) {
        this.photoRepository = repository;
    }

    @Override
    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }
}
