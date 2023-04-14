package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByRole(String role);
}