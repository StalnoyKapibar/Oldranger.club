package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.dto.RoleDto;

import java.util.List;
public interface RoleService {
    void createRole(Role role);

    void deleteById(Long id);

    List<Role> getAllRole();
    
    Role getRoleByAuthority(String authority);
}