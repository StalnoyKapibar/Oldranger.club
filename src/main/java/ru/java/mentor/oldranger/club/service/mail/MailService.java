package ru.java.mentor.oldranger.club.service.mail;

import java.util.Map;

public interface MailService {
    void send(String to, String subject, String message);

    String sendHtmlEmail(String to, String senderName, String fileName, String link);

    Map<String, Integer> getCountTopicsAndActiveChats(String email);
}