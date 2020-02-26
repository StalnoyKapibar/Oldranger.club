package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.PhotoCommentDto;
import ru.java.mentor.oldranger.club.model.media.Photo;

public interface PhotoCommentRepository extends JpaRepository<PhotoCommentDto, Long> {

    @Query(value = "select new ru.java.mentor.oldranger.club.dto.PhotoCommentDto (p.position, p.id, p.user, p.dateTime, ph.commentCount, p.commentText)\n" +
            "from PhotoComment p join p.photo ph where p.photo = :photo")
    Page<PhotoCommentDto> findByPhoto(Photo photo, Pageable pageable);
}
