package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UsersTreeRepository;
import ru.java.mentor.oldranger.club.dto.UsersTreeDto;
import ru.java.mentor.oldranger.club.service.user.UsersTreeService;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersTreeServiceImpl implements UsersTreeService {

    private UsersTreeRepository usersTreeRepository;

    @Override
    public List<UsersTreeDto> getInvitedUsersTreeById(long userId, long deepTree) {
        return usersTreeRepository.getInvitedUsersTreeByIdUserDto(userId, deepTree);
    }
}
