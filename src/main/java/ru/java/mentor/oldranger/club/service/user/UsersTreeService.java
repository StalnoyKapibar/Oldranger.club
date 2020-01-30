package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.dto.UsersTreeDto;

import java.util.List;

public interface UsersTreeService {

    List<UsersTreeDto> getInvitedUsersTreeById(long userId);
}
