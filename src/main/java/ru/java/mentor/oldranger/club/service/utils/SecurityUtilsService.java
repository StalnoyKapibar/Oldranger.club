package ru.java.mentor.oldranger.club.service.utils;

import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;

public interface SecurityUtilsService {
    boolean isAuthorityReachableForLoggedUser(Role role);
    /**
     * Авторизованный пользователь не ниже ROLE_USER
     */
    boolean isLoggedUserIsUser();
    User getLoggedUser();
}
