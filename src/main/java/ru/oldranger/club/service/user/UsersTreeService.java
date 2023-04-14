package ru.oldranger.club.service.user;

import ru.oldranger.club.dto.UsersTreeDto;

import java.util.List;

public interface UsersTreeService {

    List<UsersTreeDto> getInvitedUsersTreeById(long userIdm, long deepTree);
}
