package ru.java.mentor.oldranger.club.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.SubscriptionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.*;
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
    private SubsectionService subsectionService;
    private TopicService topicService;
    private CommentService commentService;
    private SubscriptionService subscriptionService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleService roleService,
                           UserService userService,
                           UserProfileService userProfileService,
                           UserStatisticService userStatisticService,
                           SectionService sectionService,
                           SubsectionService subsectionService,
                           TopicService topicService,
                           CommentService commentService,
                           SubscriptionService subscriptionService) {
        this.roleService = roleService;
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.userStatisticService = userStatisticService;
        this.sectionService = sectionService;
        this.subsectionService = subsectionService;
        this.topicService = topicService;
        this.commentService = commentService;
        this.subscriptionService = subscriptionService;
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
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRegDate(LocalDateTime.of(2019, 10, 31, 21, 33, 35));
        User moderator = new User("Moderator", "Moderator", "moderator@javamentor.com", "Moderator", roleModerator);
        moderator.setRegDate(LocalDateTime.of(2019, 10, 1, 21, 33, 35));
        User user = new User("User", "User", "user@javamentor.com", "User", roleUser);
        user.setPassword(passwordEncoder.encode("user"));
        user.setRegDate(LocalDateTime.of(2019, 11, 2, 11, 10, 35));
        User unverified = new User("Unverified", "Unverified", "unverified@javamentor.com", "Unverified", roleUnverified);
        admin.setRegDate(LocalDateTime.now());
        userService.save(admin);
        userService.save(moderator);
        userService.save(user);
        userService.save(unverified);

        // Создаем статистику пользователей
        userStatisticService.saveUserStatic(new UserStatistic(admin));
        userStatisticService.saveUserStatic(new UserStatistic(user));
        userStatisticService.saveUserStatic(new UserStatistic(moderator));
        userStatisticService.saveUserStatic(new UserStatistic(unverified));

        User andrew = new User("Andrew", "Ko", "kurgunu@gmail.com", "Andrew", roleAdmin);
        andrew.setPassword(passwordEncoder.encode("developer"));
        userService.save(andrew);

        // Создаем разделы, топики и сообщения форума;
        Section sectionForUnverified = new Section("Для всех обо всем", 1, false);
        Section sectionForUsers = new Section("Только для пользователей", 2, true);
        sectionService.addSection(sectionForUnverified);
        sectionService.addSection(sectionForUsers);

        LocalDateTime startTime = LocalDateTime.of(2019, 10, 31, 21, 33, 35);
        LocalDateTime lastMessage = LocalDateTime.now();

        // Создание подсекций
        Subsection subsection = new Subsection("Общая подсекция в секции для всех", sectionForUnverified, false);
        Subsection subsection2 = new Subsection("Подсекция для пользователей в секции для всех", sectionForUnverified, true);
        Subsection subsection3 = new Subsection("Общая подсекция в секции для пользователей", sectionForUsers, false);
        Subsection subsection4 = new Subsection("Подсекция для пользователей в секции для пользователей",sectionForUsers, true);
        subsectionService.createSubsection(subsection);
        subsectionService.createSubsection(subsection2);
        subsectionService.createSubsection(subsection3);
        subsectionService.createSubsection(subsection4);


        Topic topic = new Topic("Первый топик для всех в общей секции", admin, startTime, lastMessage, subsection, false);
        Topic topic2 = new Topic("Второй топик для зарегистрированных пользователей в общей секции", user, startTime, lastMessage, subsection, true);
        Topic topic3 = new Topic("Третий топик в секции для юзеров", moderator, startTime, lastMessage, subsection2, false);
        Topic topic4 = new Topic("Четвертый топик в секции для юзеров", user, startTime, lastMessage, subsection2, true);
        Topic topic5 = new Topic("Пятый топик", admin, startTime, lastMessage, subsection3, false);
        Topic topic6 = new Topic("Шестой топик", user, startTime, lastMessage, subsection3, true);
        Topic topic7 = new Topic("Седьмой топик", moderator, startTime, lastMessage, subsection4, false);
        Topic topic8 = new Topic("Восьмой топик", user, startTime, lastMessage, subsection4, true);
        topicService.createTopic(topic);
        topicService.createTopic(topic2);
        topicService.createTopic(topic3);
        topicService.createTopic(topic4);
        topicService.createTopic(topic5);
        topicService.createTopic(topic6);
        topicService.createTopic(topic7);
        topicService.createTopic(topic8);

        for (int i = 0; i < 10; i++) {
            Topic topicX = new Topic("topic subscription and order " + i, admin, startTime, lastMessage, subsection, false);
            topicService.createTopic(topicX);
            subscriptionService.subscribeUserOnTopic(admin, topicX);
        }

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