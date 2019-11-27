package ru.java.mentor.oldranger.club.dao.UserRepository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.media.Media;


public interface MediaRepository extends JpaRepository<Media, Long> {
}
