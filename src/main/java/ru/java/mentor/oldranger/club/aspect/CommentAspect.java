package ru.java.mentor.oldranger.club.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Aspect
@Component
public class CommentAspect {
    @Autowired
    private UserStatisticService userStatisticService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;

    @Pointcut("execution(* ru.java.mentor.oldranger.club.service.forum.impl.CommentServiceImpl.createComment(..))")
    public void addComment() {
    }

    @AfterReturning(value = "addComment()")
    public void getRoleForAddComment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Comment comment = (Comment) args[0];
        try {
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream("src/main/resources/config/dataForChangeRole.properties");
            properties.load(fis);
            long minMessageForOld_time = Long.parseLong(properties.getProperty("minNumberMessageForOld_time"));
            long maxMessageForOld_time = Long.parseLong(properties.getProperty("maxNumberMessageForOld_time"));
            User user = comment.getUser();
            UserStatistic userStatistic = userStatisticService.getUserStaticByUser(user);
            long messages = userStatistic.getMessageCount();
            if (messages >= minMessageForOld_time & messages <= maxMessageForOld_time & !user.getRole().getRole().equals("ROLE_ADMIN") & !user.getRole().getRole().equals("ROLE_MODERATOR")) {
                user.setRole(roleRepository.findRoleByRole("ROLE_OLD_TIMER"));
                userService.save(user);
            }
            long messageForVeteran = Long.parseLong(properties.getProperty("numberMessageForVeteran"));
            if (messages >= messageForVeteran & !user.getRole().getRole().equals("ROLE_ADMIN") & !user.getRole().getRole().equals("ROLE_MODERATOR")) {
                user.setRole(roleRepository.findRoleByRole("ROLE_VETERAN"));
                userService.save(user);
            }
        } catch (IOException e) {
            System.out.println();
        }
    }
}
