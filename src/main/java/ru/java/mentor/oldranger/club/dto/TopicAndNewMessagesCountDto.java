package ru.java.mentor.oldranger.club.dto;

import lombok.Getter;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.forum.Topic;

@Getter
@Setter
public class TopicAndNewMessagesCountDto extends Topic {
    private boolean hasNewMessages;
    private long newMessagesCount;
}
