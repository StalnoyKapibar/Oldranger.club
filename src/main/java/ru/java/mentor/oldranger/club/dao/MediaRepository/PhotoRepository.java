package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.media.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
