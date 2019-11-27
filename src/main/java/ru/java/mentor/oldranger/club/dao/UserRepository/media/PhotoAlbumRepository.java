package ru.java.mentor.oldranger.club.dao.UserRepository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {
}
