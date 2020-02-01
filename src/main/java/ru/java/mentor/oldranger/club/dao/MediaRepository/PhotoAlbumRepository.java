package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {

    public List<PhotoAlbum> findPhotoAlbumByViewersContainsOrViewersIsNull(User user);

}
