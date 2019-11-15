package ru.java.mentor.oldranger.club.service.utils.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.Collection;

@Service
public class SecurityUtilsServiceImpl implements SecurityUtilsService {

    @Autowired
    @Lazy
    private RoleHierarchy roleHierarchy;

    @Autowired
    private UserService userService;

    @Override
    public boolean isAuthorityReachableForLoggedUser(Role role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        return reachableGrantedAuthorities.contains(role);
    }

    @Override
    public boolean isLoggedUserIsUser() {
        return isAuthorityReachableForLoggedUser(new Role("ROLE_USER"));
    }

    @Override
    public User getLoggedUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userService.getUserByNickName(username);
    }
}
