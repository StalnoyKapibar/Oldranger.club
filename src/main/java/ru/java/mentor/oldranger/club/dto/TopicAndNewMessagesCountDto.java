package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.forum.Topic;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicAndNewMessagesCountDto {
    private Topic topic;
    private long totalMessages;
    private Boolean isSubscribed;
    private Boolean hasNewMessages;
    private Long newMessagesCount;
}
