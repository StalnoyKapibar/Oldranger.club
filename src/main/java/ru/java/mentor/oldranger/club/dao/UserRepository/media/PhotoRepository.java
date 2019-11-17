package ru.java.mentor.oldranger.club.dao.UserRepository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.user.media.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
