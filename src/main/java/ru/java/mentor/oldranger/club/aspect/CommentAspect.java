package ru.java.mentor.oldranger.club.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

@Aspect
@Component
public class CommentAspect {
    @Autowired
    private UserStatisticService userStatisticService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Value("${minNumberMessageForOld_time}")
    private String minNumberMessageForOld_time;
    @Value("${maxNumberMessageForOld_time}")
    private String maxNumberMessageForOld_time;
    @Value("${numberMessageForVeteran}")
    private String numberMessageForVeteran;

    @Pointcut("execution(* ru.java.mentor.oldranger.club.service.forum.impl.CommentServiceImpl.createComment(..))")
    public void addComment() {
    }

    @AfterReturning(value = "addComment()")
    public void getRoleAfterAddComment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Comment comment = (Comment) args[0];
        User user = comment.getUser();
        Role role = user.getRole();
        long countMessages = userStatisticService.getUserStaticByUser(user).getMessageCount();
        if (!role.getRole().equals("ROLE_ADMIN")
                & !role.getRole().equals("ROLE_MODERATOR")) {
            Role newRole = getRole(countMessages);
            if (newRole.getRole() != null && !role.equals(newRole)) {
                user.setRole(newRole);
                userService.save(user);
            }
        }
    }

    public Role getRole(long countMessages) {
        Role role = new Role();
        if (countMessages >= Long.parseLong(minNumberMessageForOld_time)
                & countMessages <= Long.parseLong(maxNumberMessageForOld_time)) {
            role = roleService.getRoleByAuthority("ROLE_OLD_TIMER");
        } else if (countMessages >= Long.parseLong(numberMessageForVeteran)) {
            role = roleService.getRoleByAuthority("ROLE_VETERAN");
        }
        return role;
    }
}
