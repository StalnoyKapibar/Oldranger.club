package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;
import java.util.TreeMap;

public interface UsersTreeService {

    TreeMap<String, List<User>> getInvitedUsersTreeById(String user, long deepTree);
}
