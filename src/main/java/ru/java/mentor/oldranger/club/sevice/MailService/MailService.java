package ru.java.mentor.oldranger.club.sevice.MailService;

public interface MailService {
    void send(String to, String subject, String message);
}
