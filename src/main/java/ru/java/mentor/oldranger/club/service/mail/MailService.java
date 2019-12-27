package ru.java.mentor.oldranger.club.service.mail;

import ru.java.mentor.oldranger.club.model.utils.EmailDraft;

public interface MailService {
    void send(String to, String subject, String message);

    String sendHtmlEmail(String to, String senderName, String fileName, String link);

    void sendHtmlMessage(String[] to, EmailDraft mail);
}