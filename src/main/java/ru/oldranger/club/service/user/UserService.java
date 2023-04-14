package ru.oldranger.club.service.user;

import ru.oldranger.club.dto.UpdateProfileDto;
import ru.oldranger.club.dto.UserAuthDTO;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long theId);

    void save(User user);

    void updateUser(User user, UpdateProfileDto updateProfileDto);

    void deleteById(Long theId);

    User getUserByNickName(String login);

    User getUserByEmail(String email);

    User getUserByEmailOrNickName(String login);

    User getUserByInviteKey(String key);

    UserAuthDTO buildUserDtoByUser(User user, boolean currentUser);

    Long getCount();
}