package ru.java.mentor.oldranger.club.service.media;

import ru.java.mentor.oldranger.club.model.media.Photo;

public interface PhotoService {
    Photo save(Photo photo);

    Photo findById(Long id);

    void deletePhoto(Long id);
}
