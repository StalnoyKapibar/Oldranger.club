package ru.java.mentor.oldranger.club.sevice.UserService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.sevice.UserService.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
}
