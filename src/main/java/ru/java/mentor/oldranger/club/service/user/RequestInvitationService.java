package ru.java.mentor.oldranger.club.service.user;


import ru.java.mentor.oldranger.club.model.user.RequestInvitation;

import java.util.List;

public interface RequestInvitationService {
    void save(RequestInvitation requestInvitation);

    List<RequestInvitation> findAll();

    void deleteById(Long theId);

}
