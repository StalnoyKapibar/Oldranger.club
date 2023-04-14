package ru.oldranger.club.service.user;

import ru.oldranger.club.model.user.Role;

public interface RoleService {
    void createRole(Role role);

    Role getRoleByAuthority(String authority);
}