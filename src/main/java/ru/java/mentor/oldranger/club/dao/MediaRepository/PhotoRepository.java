package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByAlbum(PhotoAlbum album);
}
