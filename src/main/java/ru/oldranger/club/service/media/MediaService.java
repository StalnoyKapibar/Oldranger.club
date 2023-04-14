package ru.oldranger.club.service.media;

import ru.oldranger.club.model.media.Media;
import ru.oldranger.club.model.user.User;

public interface MediaService {
    Media save(Media media);

    Media findMediaByUser(User user);

}
