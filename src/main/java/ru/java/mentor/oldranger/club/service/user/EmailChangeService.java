package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.EmailChangeToken;

public interface EmailChangeService {
    void save(EmailChangeToken emailChangeToken);

    EmailChangeToken getEmailChangeDtoByKey(String key);

    String generateMD5Key(String newEmail);
}
