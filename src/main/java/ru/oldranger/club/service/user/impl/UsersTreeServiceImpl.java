package ru.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.UserRepository.UsersTreeRepository;
import ru.oldranger.club.dto.UsersTreeDto;
import ru.oldranger.club.service.user.UsersTreeService;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersTreeServiceImpl implements UsersTreeService {

    private final UsersTreeRepository usersTreeRepository;

    @Override
    public List<UsersTreeDto> getInvitedUsersTreeById(long userId, long deepTree) {
        return usersTreeRepository.getInvitedUsersTreeByIdUserDto(userId, deepTree);
    }
}
