package ru.oldranger.club.service.utils;

import ru.oldranger.club.model.user.Role;
import ru.oldranger.club.model.user.RoleType;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface SecurityUtilsService {
    boolean isAuthorityReachableForLoggedUser(Role role);

    /**
     * Авторизованный пользователь не ниже ROLE_USER
     */
    boolean isLoggedUserIsUser();

    boolean isAdmin();

    boolean isModerator();

    User getLoggedUser();

    List<Long> getUsersFromSessionRegistry();

    boolean isAuthorityReachableForLoggedUser(RoleType role);
}
