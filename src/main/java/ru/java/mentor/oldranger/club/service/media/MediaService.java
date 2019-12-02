package ru.java.mentor.oldranger.club.service.media;

import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.User;

public interface MediaService {
    Media save(Media media);

    Media findMediaByUser(User user);

}
