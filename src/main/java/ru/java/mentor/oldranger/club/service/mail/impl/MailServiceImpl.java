package ru.java.mentor.oldranger.club.service.mail.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.java.mentor.oldranger.club.dto.RequestRegistrationDto;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @NonNull
    private JavaMailSender mailSender;
    @NonNull
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;
    @NonNull
    private UserService userService;

    @Override
    public void send(String to, String subject, String message) {
        log.debug("Sending simple email message");
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(username);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            log.debug("Message send");
        } catch (MailSendException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String sendHtmlEmail(String to, String senderName, String fileName, String link) {
        log.debug("Sending html email message to single user");
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(to);
            helper.setFrom(username);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = LocalDateTime.now().format(formatter);
            Context context = new Context();
            context.setVariable("senderName", senderName);
            context.setVariable("date", date);
            context.setVariable("link", link);
            String htmlContent = this.templateEngine.process(fileName, context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.debug("Message send");
            return "1";
        } catch (MailSendException | MessagingException e) {
            log.error(e.getMessage(), e);
            return "0";
        }
    }

    @Override
    @Async
    public void sendHtmlMessage(String[] to, EmailDraft mail) {
        log.debug("Sending html email message to multiple users");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Map<String, Object> model = new HashMap<>();
            model.put("content", mail.getMessage());
            Context context = new Context();
            context.setVariables(model);
            String html = templateEngine.process("email-template", context);
            helper.setTo(to);
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(username);
            mailSender.send(message);
            log.debug("Message send");
        } catch (MailSendException | MessagingException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void sendHtmlMessage(String to, Map<String, Object> attributes, String fileName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            Context context = new Context();
            context.setVariables(attributes);

            String htmlContent = this.templateEngine.process(fileName, context);

            helper.setTo(to);
            helper.setText(htmlContent, true);
            helper.setFrom(username);
            mailSender.send(mimeMessage);
        } catch (MailSendException | MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sendMessageByRoles(List<Role> roles, RequestRegistrationDto registrationUserDto) {
        log.debug("Sending html email message to users with different roles");
        List<User> users = userService.findAll();
        List<String> mailList = new ArrayList<>();
        for (Role role : roles) {
            users.stream().filter(user -> user.getRole().equals(role)).forEach(user -> mailList.add(user.getEmail()));
        }
        try {
            for (String mail : mailList) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
                Context context = new Context();
                context.setVariable("firstName", registrationUserDto.getFirstName());
                context.setVariable("lastName", registrationUserDto.getLastName());
                context.setVariable("email", registrationUserDto.getEmail());
                context.setVariable("about", registrationUserDto.getAbout());

                String htmlContent = this.templateEngine.process("registerNewUser.html", context);

                helper.setTo(mail);
                helper.setText(htmlContent, true);
                helper.setFrom(registrationUserDto.getEmail());
                mailSender.send(mimeMessage);
            }
            log.debug("Messages sent");
            return "1";
        } catch (MailSendException | MessagingException e) {
            e.printStackTrace();
            return "0";
        }
    }
}