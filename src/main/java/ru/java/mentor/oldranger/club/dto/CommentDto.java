package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String nickName;
    private String roleName;
    private String smallAvatar;
    private String timeSinceRegistration;
    private LocalDateTime commentDateTime;
    private Long messageCount;

    private LocalDateTime replyDateTime;
    private String replyNick;
    private String replyText;

    private String commentText;
}
