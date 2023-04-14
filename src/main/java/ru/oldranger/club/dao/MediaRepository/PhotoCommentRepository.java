package ru.oldranger.club.dao.MediaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.comment.PhotoComment;
import ru.oldranger.club.model.media.Photo;

public interface PhotoCommentRepository extends JpaRepository<PhotoComment, Long> {
    Page<PhotoComment> findByPhoto(Photo photo, Pageable pageable);

}
