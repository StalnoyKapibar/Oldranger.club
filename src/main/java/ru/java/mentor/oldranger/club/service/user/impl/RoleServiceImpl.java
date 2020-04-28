package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.dto.RoleDTO;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.service.user.RoleService;

import java.util.List;

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
    public void deleteById(Long id) {
        log.info("Deleting role with id = {}", id);
        if(id == null){
            throw new NullPointerException("Not id");
        }
        try {
            roleRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Role> getAllRole() {
        log.debug("Getting all Roles");
        List<Role> roles = null;
        try{
            roles = roleRepository.findAll();

            if(roles == null){
                throw new NullPointerException("Not Roles");
            }

            log.debug("Returned list of {} roles", roles.size());
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return roles;
    }

    @Override
    public Role getRoleByAuthority(String authority) {
        return roleRepository.findRoleByRole(authority);
    }
}