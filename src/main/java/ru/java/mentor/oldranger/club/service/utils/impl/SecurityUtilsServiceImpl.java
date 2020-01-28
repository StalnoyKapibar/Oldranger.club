package ru.java.mentor.oldranger.club.service.utils.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.RoleType;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityUtilsServiceImpl implements SecurityUtilsService {

    @Autowired
    @Lazy
    private RoleHierarchy roleHierarchy;

    @NonNull
    private UserService userService;
    @NonNull
    private SessionRegistry sessionRegistry;

    @Override
    public List<Long> getUsersFromSessionRegistry() {
        log.debug("Getting users from session registry");
        List<Long> usersIds = new ArrayList<>();
        try {
            for (Object principal : sessionRegistry.getAllPrincipals()) {
                if (principal instanceof User) {
                    usersIds.add(((User) principal).getId());
                }
            }
            log.debug("Returned list of {} ids", usersIds.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return usersIds;
    }

    @Override
    public boolean isAuthorityReachableForLoggedUser(Role role) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        return reachableGrantedAuthorities.contains(role);
    }

    @Override
    public boolean isAuthorityReachableForLoggedUser(RoleType role) {
        Role userRole;
        switch (role) {
            case ROLE_USER: {
                userRole = new Role("ROLE_USER");
                userRole.setId(3L);
            }
            break;
            case ROLE_MODERATOR:{
                userRole = new Role("ROLE_MODERATOR");
                userRole.setId(2L);
                break;
            }
            case ROLE_ADMIN:{
                userRole = new Role("ROLE_ADMIN");
                userRole.setId(1L);
                break;
            }
            default:{
                userRole = new Role("ROLE_PROSPECT");
                userRole.setId(4L);
                break;
            }
        }

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        return reachableGrantedAuthorities.contains(userRole);
    }

    @Override
    public boolean isLoggedUserIsUser() {
        return isAuthorityReachableForLoggedUser(new Role("ROLE_USER"));
    }

    @Override
    public boolean isAdmin() {
        return isAuthorityReachableForLoggedUser(RoleType.ROLE_ADMIN);
    }

    @Override
    public boolean isModerator() {
        return isAuthorityReachableForLoggedUser(RoleType.ROLE_MODERATOR);
    }

    @Override
    public User getLoggedUser() {
        log.debug("Getting logged user");
        User user = null;
        try {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) return null;
            String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            user = userService.getUserByNickName(username);
            log.debug("Returned user {}", user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return user;
    }
}