package ru.java.mentor.oldranger.club.service.media;

import org.springframework.core.io.Resource;

public interface FileInChatService {
    Resource loadFileAsResource(String fileName);

}
