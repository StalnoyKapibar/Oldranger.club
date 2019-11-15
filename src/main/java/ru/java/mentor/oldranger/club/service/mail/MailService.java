package ru.java.mentor.oldranger.club.service.mail;

public interface MailService {
    void send(String to, String subject, String message);

    String sendHtmlEmail(String to, String subject, String fileName, String link);
}