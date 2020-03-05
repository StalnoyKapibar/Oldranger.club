package ru.java.mentor.oldranger.club.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.java.mentor.oldranger.club.dao.ForumRepository.DirectionRepository;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.mail.Direction;
import ru.java.mentor.oldranger.club.model.mail.DirectionType;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.forum.*;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@ConditionalOnMissingClass({"org.junit.Test"})
public class DataInitializer implements CommandLineRunner {
    private RoleService roleService;
    private UserService userService;
    private SectionService sectionService;
    private SubsectionService subsectionService;
    private TopicService topicService;
    private CommentService commentService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private BlackListService blackListService;
    private ChatService chatService;
    private WritingBanService writingBanService;
    private DirectionRepository directionRepository;
    private ArticleTagService articleTagService;
    private ArticleTagsNodeService articleTagsNodeService;
    private ArticleService articleService;
    private MediaService mediaService;
    private PhotoAlbumService albumService;
    private InvitationService invitationService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleService roleService,
                           UserService userService,
                           SectionService sectionService,
                           SubsectionService subsectionService,
                           TopicService topicService,
                           CommentService commentService,
                           TopicVisitAndSubscriptionService topicVisitAndSubscriptionService,
                           BlackListService blackListService,
                           ChatService chatService,
                           WritingBanService writingBanService,
                           DirectionRepository directionRepository,
                           ArticleTagService articleTagService,
                           ArticleTagsNodeService articleTagsNodeService,
                           ArticleService articleService,
                           MediaService mediaService,
                           PhotoAlbumService albumService,
                           InvitationService invitationService) {
        this.roleService = roleService;
        this.userService = userService;
        this.sectionService = sectionService;
        this.subsectionService = subsectionService;
        this.topicService = topicService;
        this.commentService = commentService;
        this.topicVisitAndSubscriptionService = topicVisitAndSubscriptionService;
        this.blackListService = blackListService;
        this.chatService = chatService;
        this.writingBanService = writingBanService;
        this.directionRepository = directionRepository;
        this.articleTagService = articleTagService;
        this.articleTagsNodeService = articleTagsNodeService;
        this.articleService = articleService;
        this.mediaService = mediaService;
        this.albumService = albumService;
        this.invitationService = invitationService;
    }

    @Override
    public void run(String... args) throws Exception {

        // Создаем тестовые роли, сохраняем в репозиторий ролей;
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleModerator = new Role("ROLE_MODERATOR");
        Role roleUser = new Role("ROLE_USER");
        Role roleProspect = new Role("ROLE_PROSPECT");
        Role roleOld_Timer = new Role("ROLE_OLD_TIMER");
        Role roleVeteran = new Role("ROLE_VETERAN");
        roleService.createRole(roleAdmin);
        roleService.createRole(roleModerator);
        roleService.createRole(roleUser);
        roleService.createRole(roleProspect);
        roleService.createRole(roleOld_Timer);
        roleService.createRole(roleVeteran);

        // Создаем пользователей с разными ролями;
        User admin = new User("Admin", "Admin", "admin@javamentor.com", "Admin", roleAdmin);
        admin.setRegDate(LocalDateTime.of(2019, 10, 31, 21, 33, 35));
        admin.setPassword(passwordEncoder.encode("admin"));
        User moderator = new User("Moderator", "Moderator", "moderator@javamentor.com", "Moderator", roleModerator);
        moderator.setRegDate(LocalDateTime.of(2019, 10, 1, 21, 33, 35));
        moderator.setPassword(passwordEncoder.encode("moderator"));
        User user = new User("User", "User", "user@javamentor.com", "User", roleUser);
        user.setRegDate(LocalDateTime.of(2019, 11, 2, 11, 10, 35));
        user.setPassword(passwordEncoder.encode("user"));
        User unverified = new User("Prospect", "Prospect", "prospect@javamentor.com", "Prospect", roleProspect);
        unverified.setPassword(passwordEncoder.encode("prospect"));
        userService.save(admin);
        userService.save(moderator);
        userService.save(user);
        userService.save(unverified);

        // Создаем пользователей, которые зарегистрировались по приглашениям
        //1.Создаём ползователя, который раздает приглашения
        User invitingUser = new User("invitingUser", "invitingUser", "invitingUser@javamentor.com", "invitingUser", roleUser);
        invitingUser.setRegDate(LocalDateTime.of(2020, 2, 20, 11, 10, 35));
        invitingUser.setPassword(passwordEncoder.encode("invitingUser"));
        userService.save(invitingUser);
        //2. Создаем токен приглашения
        String keyOne = invitationService.generateKey();
        InvitationToken invitationToken = new InvitationToken(keyOne, invitingUser);

        //3. Создаём приглашённого пользователя по токену
        //3.1 Создаём 1-го приглашенного юзера
        User newInviteUserOne = new User();
        newInviteUserOne.setEmail("inviteOne@javamentor.com");
        newInviteUserOne.setNickName("inviteOne");
        newInviteUserOne.setFirstName("inviteOneF");
        newInviteUserOne.setLastName("inviteOneL");
        newInviteUserOne.setPassword(passwordEncoder.encode("One"));
        newInviteUserOne.setRegDate(LocalDateTime.now());
        newInviteUserOne.setInvite(keyOne);
        newInviteUserOne.setRole(roleService.getRoleByAuthority("ROLE_PROSPECT"));
        userService.save(newInviteUserOne);

        invitationToken.setVisitor(newInviteUserOne);
        invitationToken.setMail(newInviteUserOne.getEmail());
        invitationToken.setUsed(true);
        invitationService.save(invitationToken);

        //3.2 Создаём 2-го приглашенного юзера
        String keyTwo = invitationService.generateKey();
        InvitationToken invitationTokenTwo = new InvitationToken(keyTwo, invitingUser);

        User newInviteUserTwo = new User();
        newInviteUserTwo.setEmail("inviteTwo@javamentor.com");
        newInviteUserTwo.setNickName("inviteTwo");
        newInviteUserTwo.setFirstName("inviteTwoF");
        newInviteUserTwo.setLastName("inviteTwoL");
        newInviteUserTwo.setPassword(passwordEncoder.encode("Two"));
        newInviteUserTwo.setRegDate(LocalDateTime.now());
        newInviteUserTwo.setInvite(keyTwo);
        newInviteUserTwo.setRole(roleService.getRoleByAuthority("ROLE_PROSPECT"));
        userService.save(newInviteUserTwo);

        invitationTokenTwo.setVisitor(newInviteUserTwo);
        invitationTokenTwo.setMail(newInviteUserTwo.getEmail());
        invitationTokenTwo.setUsed(true);
        invitationService.save(invitationTokenTwo);

        //4. Создаём приглашённого пользователя по email
        //4.1. Создаём 1-го приглашённого пользователя по email
        String emailThree = "inviteOneByEmail@javamentor.com";
        User inviteUserThreeByEmail = new User();
        String keyEmailOne = invitationService.generateMD5Key(emailThree);
        InvitationToken invitationTokenByEmail = new InvitationToken(keyEmailOne, invitingUser, emailThree);

        inviteUserThreeByEmail.setNickName("inviteThreeByEmail");
        inviteUserThreeByEmail.setEmail(emailThree);
        inviteUserThreeByEmail.setFirstName("inviteThreeByEmailF");
        inviteUserThreeByEmail.setLastName("inviteThreeByEmailL");
        inviteUserThreeByEmail.setPassword(passwordEncoder.encode("ThreeEmail"));
        inviteUserThreeByEmail.setRegDate(LocalDateTime.now());
        inviteUserThreeByEmail.setInvite(keyEmailOne);
        inviteUserThreeByEmail.setRole(roleService.getRoleByAuthority("ROLE_PROSPECT"));
        userService.save(inviteUserThreeByEmail);

        invitationTokenByEmail.setVisitor(inviteUserThreeByEmail);
        invitationTokenByEmail.setMail(inviteUserThreeByEmail.getEmail());
        invitationTokenByEmail.setUsed(true);
        invitationService.save(invitationTokenByEmail);

        // Общий чат
        Chat chat = new Chat();
        PhotoAlbum photoAlbum = new PhotoAlbum("Альбом общего чата");
        photoAlbum.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        albumService.save(photoAlbum);

        chat.setPhotoAlbum(photoAlbum);

        chatService.createChat(chat);

        List<User> users = userService.findAll();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (User iUser : users) {
            Direction direction = new Direction();
            direction.setLastSendTime(localDateTime);
            direction.setUser(iUser);
            direction.setDirectionType(DirectionType.NEVER);
            directionRepository.save(direction);
        }

        //Добавляем User в чёрный список
        BlackList blackList = new BlackList(user, null);
        blackListService.save(blackList);

        //Запрещаем пользователью отправлять личные сообщения
        writingBanService.save(new WritingBan(user, BanType.ON_CHAT, LocalDateTime.of(2019, 11, 28, 19, 10, 0)));

        User andrew = new User("Andrew", "Ko", "kurgunu@gmail.com", "Andrew", roleUser);
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
        Subsection subsection = new Subsection("Общая подсекция в секции для всех", 1, sectionForUnverified, false);
        Subsection subsection2 = new Subsection("Подсекция для пользователей в секции для всех", 2, sectionForUnverified, true);
        Subsection subsection3 = new Subsection("Общая подсекция в секции для пользователей", 1, sectionForUsers, false);
        Subsection subsection4 = new Subsection("Подсекция для пользователей в секции для пользователей", 2, sectionForUsers, true);
        subsectionService.createSubsection(subsection);
        subsectionService.createSubsection(subsection2);
        subsectionService.createSubsection(subsection3);
        subsectionService.createSubsection(subsection4);

        Topic topic = new Topic("Первый топик для всех в общей секции", admin, startTime, lastMessage, subsection, false, false);
        PhotoAlbum photoAlbum1 = new PhotoAlbum("PhotoAlbum by " + topic.getName());
        photoAlbum1.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic2 = new Topic("Второй топик для зарегистрированных пользователей в общей секции", user, startTime, lastMessage, subsection, true, false);
        PhotoAlbum photoAlbum2 = new PhotoAlbum("PhotoAlbum by " + topic2.getName());
        photoAlbum2.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic3 = new Topic("Третий топик в секции для юзеров", moderator, startTime, lastMessage, subsection2, false, false);
        PhotoAlbum photoAlbum3 = new PhotoAlbum("PhotoAlbum by " + topic3.getName());
        photoAlbum3.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic4 = new Topic("Четвертый топик в секции для юзеров", user, startTime, lastMessage, subsection2, true, false);
        PhotoAlbum photoAlbum4 = new PhotoAlbum("PhotoAlbum by " + topic4.getName());
        photoAlbum4.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic5 = new Topic("Пятый топик", admin, startTime, lastMessage, subsection3, false, false);
        PhotoAlbum photoAlbum5 = new PhotoAlbum("PhotoAlbum by " + topic5.getName());
        photoAlbum5.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic6 = new Topic("Шестой топик", user, startTime, lastMessage, subsection3, true, false);
        PhotoAlbum photoAlbum6 = new PhotoAlbum("PhotoAlbum by " + topic6.getName());
        photoAlbum6.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic7 = new Topic("Седьмой топик", moderator, startTime, lastMessage, subsection4, false, true);
        PhotoAlbum photoAlbum7 = new PhotoAlbum("PhotoAlbum by " + topic7.getName());
        photoAlbum7.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        Topic topic8 = new Topic("Восьмой топик", user, startTime, lastMessage, subsection4, true, false);
        PhotoAlbum photoAlbum8 = new PhotoAlbum("PhotoAlbum by " + topic8.getName());
        photoAlbum8.setMedia(mediaService.findMediaByUser(userService.getUserByNickName("Admin")));
        topicService.createTopic(topic);
        topicService.createTopic(topic2);
        topicService.createTopic(topic3);
        topicService.createTopic(topic4);
        topicService.createTopic(topic5);
        topicService.createTopic(topic6);
        topicService.createTopic(topic7);
        topicService.createTopic(topic8);
        albumService.createAlbum(photoAlbum1);
        albumService.createAlbum(photoAlbum2);
        albumService.createAlbum(photoAlbum3);
        albumService.createAlbum(photoAlbum4);
        albumService.createAlbum(photoAlbum5);
        albumService.createAlbum(photoAlbum6);
        albumService.createAlbum(photoAlbum7);
        albumService.createAlbum(photoAlbum8);

        boolean b = false;
        for (int i = 0; i < 2; i++) {
            b = !b;
            Random random = new Random();
            Topic topicX = new Topic("scrollable topics test " + i, admin, startTime.minusDays(i), lastMessage.minusMinutes(random.nextInt(60)), subsection, b, false);
            topicService.createTopic(topicX);
            for (int j = 0; j < 10; j++) {
                Comment commentX = new Comment(topicX, admin, null, LocalDateTime.now(), "Всем привет! #" + j);
                commentService.createComment(commentX);
            }
            if (i % 2 == 0) {
                TopicVisitAndSubscription subscription1 = new TopicVisitAndSubscription(admin, topicX, true, lastMessage.minusDays(1), lastMessage.minusMinutes(random.nextInt(60)));
                topicVisitAndSubscriptionService.save(subscription1);
            } else {
                TopicVisitAndSubscription subscription2 = new TopicVisitAndSubscription(andrew, topicX, true, lastMessage.minusDays(1), lastMessage.minusMinutes(random.nextInt(60)));
                topicVisitAndSubscriptionService.save(subscription2);
            }
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
        for (int i = 1; i < 21; i++) {
            commentService.createComment(new Comment(topic3, user, null,
                    LocalDateTime.of(2019, 11, 1, 21, 30 + i, 35),
                    "Тестовое сообщение " + i));
        }

        for (int i = 1; i < 12; i++) {
            User newuser = new User("User", "User", "user" + i + "@javamentor.com", "User" + i, roleUser);
            newuser.setRegDate(LocalDateTime.of(2019, 8, 10 + i, 11, 10, 35));
            userService.save(newuser);
        }

        ArticleTag newsTag1 = new ArticleTag("Тема 1");
        ArticleTag newsTag2 = new ArticleTag("Тема 2");
        ArticleTag newsTag3 = new ArticleTag("Тема 3");

        articleTagService.addTag(newsTag1);
        articleTagService.addTag(newsTag2);
        articleTagService.addTag(newsTag3);

        ArticleTag[] newsTags = {newsTag1, newsTag2, newsTag3};
        for (int i = 1; i < 11; i++) {

            Set<ArticleTag> tags = new HashSet<>();
            tags.add(newsTags[i % 3]);
            articleService.addArticle(new Article("news", admin, tags, LocalDateTime.of(2019, 11, 1, 21, 33 + i, 35),
                    "Text news!", false));
        }

        ArticleTagsNode articleTagsNode1 = new ArticleTagsNode(1L, null, 2, newsTag2);
        ArticleTagsNode articleTagsNode2 = new ArticleTagsNode(2L, articleTagsNode1, 1, newsTag3);
        ArticleTagsNode articleTagsNode3 = new ArticleTagsNode(3L, null, 1, newsTag1);
        ArticleTagsNode articleTagsNode4 = new ArticleTagsNode(4L, articleTagsNode3, 2, newsTag3);
        ArticleTagsNode articleTagsNode5 = new ArticleTagsNode(5L, articleTagsNode3, 1, newsTag2);
        ArticleTagsNode articleTagsNode6 = new ArticleTagsNode(6L, articleTagsNode5, 1, newsTag2);

        articleTagsNodeService.save(articleTagsNode1);
        articleTagsNodeService.save(articleTagsNode2);
        articleTagsNodeService.save(articleTagsNode3);
        articleTagsNodeService.save(articleTagsNode4);
        articleTagsNodeService.save(articleTagsNode5);
        articleTagsNodeService.save(articleTagsNode6);
    }
}