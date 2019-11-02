package ru.java.mentor.oldranger.club.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private RoleService roleService;
    private UserService userService;
    private UserProfileService userProfileService;
    private UserStatisticService userStatisticService;
    private SectionService sectionService;
    private TopicService topicService;
    private CommentService commentService;

    @Autowired
    public DataInitializer(RoleService roleService,
                           UserService userService,
                           UserProfileService userProfileService,
                           UserStatisticService userStatisticService,
                           SectionService sectionService,
                           TopicService topicService,
                           CommentService commentService) {
        this.roleService = roleService;
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.userStatisticService = userStatisticService;
        this.sectionService = sectionService;
        this.topicService = topicService;
        this.commentService = commentService;
    }

    @Override
    public void run(String... args) throws Exception {

        // Создаем тестовые роли, сохраняем в репозиторий ролей;
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleModerator = new Role("ROLE_MODERATOR");
        Role roleUser = new Role("ROLE_USER");
        Role roleUnverified = new Role("ROLE_UNVERIFIED");
        roleService.createRole(roleAdmin);
        roleService.createRole(roleModerator);
        roleService.createRole(roleUser);
        roleService.createRole(roleUnverified);

        // Создаем пользователей с разными ролями;
        User admin = new User("Admin", "Admin", "admin@javamentor.com", "Admin", roleAdmin);
        User moderator = new User("Moderator", "Moderator", "moderator@javamentor.com", "Moderator", roleModerator);
        User user = new User("User", "User", "user@javamentor.com", "User", roleUser);
        User unverified = new User("Unverified", "Unverified", "unverified@javamentor.com", "Unverified", roleUnverified);
        userService.save(admin);
        userService.save(moderator);
        userService.save(user);
        userService.save(unverified);

        // Создаем разделы, топики и сообщения форума;
        Section sectionForUnverified = new Section("Для всех обо всем", 1, false);
        Section sectionForUsers = new Section("Только для пользователей", 2, true);
        sectionService.addSection(sectionForUnverified);
        sectionService.addSection(sectionForUsers);

        LocalDateTime startTime = LocalDateTime.of(2019, 10, 31, 21, 33, 35);
        LocalDateTime lastMessage = LocalDateTime.now();

        Topic topic = new Topic("Первый топик для всех в общей секции", admin, startTime, lastMessage, sectionForUnverified, false);
        Topic topic2 = new Topic("Второй топик для зарегистрированных пользователей в общей секции", user, startTime, lastMessage, sectionForUnverified, true);
        Topic topic3 = new Topic("Третий топик в секции для юзеров", moderator, startTime, lastMessage, sectionForUsers, false);
        Topic topic4 = new Topic("Четвертый топик в секции для юзеров", user, startTime, lastMessage, sectionForUsers, true);
        topicService.createTopic(topic);
        topicService.createTopic(topic2);
        topicService.createTopic(topic3);
        topicService.createTopic(topic4);

        Comment comment1 = new Comment(topic, admin, null, LocalDateTime.now(), "Всем привет!");
        Comment comment2 = new Comment(topic, moderator, comment1, LocalDateTime.now(), "И тебе привет!");
        Comment comment3 = new Comment(topic2, user, null, LocalDateTime.now(), "Как жизнь?");
        Comment comment4 = new Comment(topic2, admin, comment3, LocalDateTime.now(), "Все гуд!");
        Comment comment5 = new Comment(topic3, user, null, LocalDateTime.now(), "Это тестовое сообщение");
        Comment comment6 = new Comment(topic3, moderator, comment5, LocalDateTime.now(), "А это ответ на тестовое сообщение");
        Comment comment7 = new Comment(topic4, admin, null, LocalDateTime.now(), "Всем юзерам привет!");
        Comment comment8 = new Comment(topic4, user, comment7, LocalDateTime.now(), "Админам привет!");
        commentService.createComment(comment1);
        commentService.createComment(comment2);
        commentService.createComment(comment3);
        commentService.createComment(comment4);
        commentService.createComment(comment5);
        commentService.createComment(comment6);
        commentService.createComment(comment7);
        commentService.createComment(comment8);

        /**
         * 20 messages for Pageable test (topic 3)
         */
        for (int i = 1; i < 21; i ++) {
            commentService.createComment(new Comment(topic3, user, null,
                    LocalDateTime.of(2019, 11, 1, 21, 30 + i, 35),
                    "Тестовое сообщение " + i));
        }

    }
}