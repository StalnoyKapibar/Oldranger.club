package ru.java.mentor.oldranger.club.service.utils.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Component("scheduler")
public class SchedulerImpl {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Value("${numberTimeForOld_timeInYears}")
    private String numberTimeForOld_timeInYears1;

    @Scheduled(cron = "${scheduler.timeForChangeRole.cron}")
    public void updateRoleVeteranForUser() {
        LocalDateTime timeNow = LocalDateTime.now();
        long numberTimeForOld_timeInYears = Long.parseLong(numberTimeForOld_timeInYears1);
        LocalDateTime registerData = timeNow.minusYears(numberTimeForOld_timeInYears);
        Role roleVeteran = roleRepository.findRoleByRole("ROLE_VETERAN");
        Role roleAdmin = roleRepository.findRoleByRole("ROLE_ADMIN");
        Role roleModerator = roleRepository.findRoleByRole("ROLE_MODERATOR");
        List<User> list = userRepository.findAllByRegDateBeforeAndRoleNotAndRoleNotAndRoleNot(registerData, roleVeteran, roleAdmin, roleModerator);
        for (User u : list) {
            u.setRole(roleVeteran);
            userService.save(u);
        }
    }
}
