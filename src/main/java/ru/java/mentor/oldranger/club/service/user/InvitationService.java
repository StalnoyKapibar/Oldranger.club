package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.Date;
import java.util.List;

public interface InvitationService {
    public void save(InvitationToken invitationToken);

    List<InvitationToken> findAll();

    InvitationToken getInvitationTokenByKey(String key);

    void markAsUsed(String key);

    String getCurrentKey(User user);

    String generateKey();

    Date getDateCreate(String key);

    void markInviteOnMailAsUsed(String mail);

    void setShelfLife(Long time);

    boolean checkShelfLife(InvitationToken token);
}
