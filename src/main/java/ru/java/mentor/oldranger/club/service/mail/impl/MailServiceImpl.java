package ru.java.mentor.oldranger.club.service.mail.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.mail.MailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Service
public class MailServiceImpl implements MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void send(String to, String subject, String message) {
        LOG.debug("Sending simple email message");
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(username);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            LOG.debug("Message send");
        } catch (MailSendException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String sendHtmlEmail(String to, String senderName, String fileName, String link) {
        LOG.debug("Sending html email message to single user");
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
            LOG.debug("Message send");
            return "1";
        } catch (MailSendException | MessagingException e) {
            LOG.error(e.getMessage(), e);
            return "0";
        }
    }

    @Override
    @Async
    public void sendHtmlMessage(String[] to, EmailDraft mail) {
        LOG.debug("Sending html email message to multiple users");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            Map<String,Object> model = new HashMap<>();
            model.put("content", mail.getMessage());
            Context context = new Context();
            context.setVariables(model);
            String html = templateEngine.process("email-template", context);
            helper.setTo(to);
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(username);
            mailSender.send(message);
            LOG.debug("Message send");
        } catch (MailSendException | MessagingException e) {
            LOG.error(e.getMessage(), e);
        }

    }
}