package ru.oldranger.club.service.mail;

import ru.oldranger.club.model.utils.EmailDraft;

import java.util.Map;

public interface MailService {
    void send(String to, String subject, String message);

    String sendHtmlEmail(String to, String senderName, String fileName, String link);

    void sendHtmlMessage(String to, Map<String, Object> attributes, String fileName);
    void sendHtmlMessage(String[] to, EmailDraft mail);
}