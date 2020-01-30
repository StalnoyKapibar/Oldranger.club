package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface UsersTreeService {

    List<User> getInvitedUsersTreeById(String user, long deepTree);

}
