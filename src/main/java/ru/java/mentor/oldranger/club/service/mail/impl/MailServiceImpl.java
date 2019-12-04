package ru.java.mentor.oldranger.club.service.mail.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.java.mentor.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.java.mentor.oldranger.club.model.mail.Direction;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.projection.IdAndNumberProjection;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.impl.TopicServiceImpl;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.impl.UserServiceImpl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TopicService service;

    @Value("${spring.mail.username}")
    private String username;


    @Override
    public void send(String to, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    @Override
    public String sendHtmlEmail(String to, String senderName, String fileName, String link) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(to);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = LocalDateTime.now().format(formatter);
            Context context = new Context();
            context.setVariable("senderName", senderName);
            context.setVariable("date", date);
            context.setVariable("link", link);
            String htmlContent = this.templateEngine.process(fileName, context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            return "1";
        } catch (MailSendException | MessagingException e) {
            return "0";
        }
    }


    public Map<String, Integer> getCountTopicsAndActiveChats(String email) {
        Map<String, Integer> countTopicsAndActiveChats = new HashMap<>();
        User user = userService.getUserByEmail(email);
        Integer newTopicsCount = service
                .getNewMessagesCountForTopicsAndUser(service
                        .getActualTopicsLimitAnyBySection(30), user).size();
        Integer countActiveChats = chatRepository.getCountChatByUserID(user.getId());

        countTopicsAndActiveChats.put("unreadTopics", newTopicsCount);
        countTopicsAndActiveChats.put("activeChats", countActiveChats);
        return countTopicsAndActiveChats;
    }
}