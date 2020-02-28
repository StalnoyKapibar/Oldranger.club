package ru.java.mentor.oldranger.club.service.mail;

import ru.java.mentor.oldranger.club.model.mail.DirectionType;
import ru.java.mentor.oldranger.club.model.user.User;

public interface MailDirectionService {
    void sendDirection();
    void changeUserDirection(User user, DirectionType type);
}
