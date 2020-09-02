package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.dto.UpdateProfileDto;
import ru.java.mentor.oldranger.club.dto.UserAuthDTO;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long theId);

    User save(User user);

    User updateUser(User user, UpdateProfileDto updateProfileDto);

    void deleteById(Long theId);

    User getUserByNickName(String login);

    User getUserByEmail(String email);

    User getUserByEmailOrNickName(String login);

    User getUserByInviteKey(String key);

    UserAuthDTO buildUserDtoByUser(User user, boolean currentUser);

    Long getCount();

    List<User> findUsersByRole(Role role);
}