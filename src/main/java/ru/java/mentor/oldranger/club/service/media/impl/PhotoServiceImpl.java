package ru.java.mentor.oldranger.club.service.media.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.MediaRepository.PhotoRepository;
import ru.java.mentor.oldranger.club.model.media.Photo;
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

    @Override
    public Photo findById(Long id) {
        return photoRepository.findById(id).get();
    }

    @Override
    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }
}
