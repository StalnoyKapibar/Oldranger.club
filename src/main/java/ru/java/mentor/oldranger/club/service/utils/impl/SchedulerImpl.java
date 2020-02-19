package ru.java.mentor.oldranger.club.service.utils.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@EnableScheduling
@PropertySource("classpath:config/dataForChangeRole.properties")
@Component("scheduler")
public class SchedulerImpl {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "${scheduler.timeForChangeRole.cron}")
    public void updateRoleVeteranForUser() {
        LocalDateTime timeNow = LocalDateTime.now();
        try {
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config/dataForChangeRole.properties");
            properties.load(fis);
            Long numberTimeForOld_timeInYears = Long.parseLong(properties.getProperty("numberTimeForOld_timeInYears"));
            LocalDateTime registerData = timeNow.minusYears(numberTimeForOld_timeInYears);
            Role roleVeteran = roleRepository.findRoleByRole("ROLE_VETERAN");
            Role roleAdmin = roleRepository.findRoleByRole("ROLE_ADMIN");
            Role roleModerator = roleRepository.findRoleByRole("ROLE_MODERATOR");
            List<User> list = userRepository.findAllByRegDateBeforeAndRoleNotAndRoleNotAndRoleNot(registerData, roleVeteran, roleAdmin, roleModerator);
            for (User u : list) {
                u.setRole(roleVeteran);
                userService.save(u);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
