
package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface InvitationService {
    void save(InvitationToken invitationToken);

    List<InvitationToken> findAll();

    InvitationToken getInvitationTokenByKey(String key);

    void markAsUsed(String key);

    String getCurrentKey(User user);

    String generateKey();

    LocalDateTime getDateCreate(String key);

    void markInviteOnMailAsUsed(String mail);

    void setShelfLife(Long time);

    boolean checkShelfLife(InvitationToken token);

}