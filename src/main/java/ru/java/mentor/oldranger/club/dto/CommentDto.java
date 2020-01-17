package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.java.mentor.oldranger.club.model.forum.ImageComment;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "This is CommentDTO, \n where answerID - ID of comment on which answer was given ",
        requiredProperties = {"author", "topicId"})
public class CommentDto {
    private Long positionInTopic;
    private Long topicId;
    private Long userId;
    private Long answerID;
    private User author;
    private LocalDateTime commentDateTime;
    private Long messageCount;
    private LocalDateTime replyDateTime;
    private String replyNick;
    private String replyText;
    @NotEmpty
    @Pattern(regexp = "^(?=\\s*\\S).*$")
    private String commentText;
    private List<ImageComment> imageComment;
}