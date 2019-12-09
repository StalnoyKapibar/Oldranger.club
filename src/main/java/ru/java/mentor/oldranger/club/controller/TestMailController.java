package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.java.mentor.oldranger.club.service.mail.MailDirectionService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.mail.impl.MailDirectionServiceImpl;

@Hidden
@Controller
@RequestMapping("/test")
public class TestMailController {
    @Autowired
    MailService mailService;

    @Autowired
    MailDirectionService mailDirectionService;

    // Пример работы с отправлением почты
    @GetMapping("/mail")
    @ResponseBody
    public String sendTestMail() {
        mailService.send("daref67649@3dmail.top", "test", "Lorem ipsum dolor sit amet");
        return "Mail send successfully";
    }

    @GetMapping("/direction")
    @ResponseBody
    public String sendTestDirection() {
        mailService.sendHtmlMessage("daref67649@3dmail.top",
                ((MailDirectionServiceImpl)mailDirectionService).getCountTopicsAndActiveChats("admin@admin.ru"),
                "directionLetter.html");
        return "Direction nice working!";
    }

}