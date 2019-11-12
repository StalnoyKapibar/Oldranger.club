package ru.java.mentor.oldranger.club.dto;

import lombok.*;
import ru.java.mentor.oldranger.club.model.forum.Topic;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TopicAndNewMessagesCountDto {
    private Topic topic;
    private long totalMessages;
    private Boolean isSubscribed;
    private Boolean hasNewMessages;
    private Long newMessagesCount;
}
