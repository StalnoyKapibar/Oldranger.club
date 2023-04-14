package ru.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.media.Media;
import ru.oldranger.club.model.user.User;


public interface MediaRepository extends JpaRepository<Media, Long> {
    Media findMediaByUser(User user);
}
