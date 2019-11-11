package ru.java.mentor.oldranger.club.service.mail;

public interface MailService {
    void send(String to, String subject, String message);

    void sendHtmlEmail(String to, String subject, String message);
}