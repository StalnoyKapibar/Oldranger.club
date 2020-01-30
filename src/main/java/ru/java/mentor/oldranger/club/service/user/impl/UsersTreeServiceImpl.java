package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.UsersTreeRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UsersTreeService;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersTreeServiceImpl implements UsersTreeService {

    private UsersTreeRepository usersTreeRepository;
    private UserRepository userRepository;

    @Override
    public List<User> getInvitedUsersTreeById(String nameUser, long deepTree) {
        User user= userRepository.findUserByNickName(nameUser);
        return usersTreeRepository.getInvitedUsersTreeById(user.getId());
    }
}
