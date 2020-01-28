package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.ImageComment;

import java.util.List;

public interface ImageCommnetService {

    ImageComment createNewImage(MultipartFile file);

    void save(ImageComment imageComment);

    List<ImageComment> findAllByCommentId(Long commentId);
}
