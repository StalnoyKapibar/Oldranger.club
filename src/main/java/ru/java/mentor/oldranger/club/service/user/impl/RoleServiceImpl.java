package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.service.user.RoleService;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    @Override
    public void createRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public Role getRoleByAuthority(String authority) {
        return roleRepository.findRoleByRole(authority);
    }
}