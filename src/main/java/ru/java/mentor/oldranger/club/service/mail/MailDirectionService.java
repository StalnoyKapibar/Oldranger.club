package ru.java.mentor.oldranger.club.service.mail;

import ru.java.mentor.oldranger.club.model.mail.DirectionType;

public interface MailDirectionService {

    void sendDirection();

    void changeUserDirection(Long userId, DirectionType type);
}
