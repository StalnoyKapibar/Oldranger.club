package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.service.user.RoleService;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);
    private RoleRepository roleRepository;

    @Override
    public void createRole(Role role) {
        LOG.info("Saving role {}", role);
        try {
            roleRepository.save(role);
            LOG.info("Role saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Role getRoleByAuthority(String authority) {
        return roleRepository.findRoleByRole(authority);
    }
}