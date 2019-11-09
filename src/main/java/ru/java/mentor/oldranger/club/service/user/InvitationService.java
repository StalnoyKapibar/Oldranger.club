package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.InvitationToken;

import java.util.List;

public interface InvitationService {
    public void save(InvitationToken invitationToken);

    List<InvitationToken> findAll();

    InvitationToken getInvitationTokenByKey(String key);

    void markAsUsed(String key);

    String getCurrentKey();
}
