package ru.oldranger.club.service.mail;

import ru.oldranger.club.model.mail.DirectionType;
import ru.oldranger.club.model.user.User;

public interface MailDirectionService {
    void sendDirection();
    void changeUserDirection(User user, DirectionType type);
}
