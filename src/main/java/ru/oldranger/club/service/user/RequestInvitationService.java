package ru.oldranger.club.service.user;


import ru.oldranger.club.model.user.RequestInvitation;

import java.util.List;

public interface RequestInvitationService {
    void save(RequestInvitation requestInvitation);

    List<RequestInvitation> findAll();

    void deleteById(Long theId);

}
