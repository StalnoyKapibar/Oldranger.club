package ru.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.UserRepository.RoleRepository;
import ru.oldranger.club.model.user.Role;
import ru.oldranger.club.service.user.RoleService;

@Slf4j
@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    @Override
    public void createRole(Role role) {
        log.info("Saving role {}", role);
        try {
            roleRepository.save(role);
            log.info("Role saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Role getRoleByAuthority(String authority) {
        return roleRepository.findRoleByRole(authority);
    }
}