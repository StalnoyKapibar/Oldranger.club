package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.java.mentor.oldranger.club.sevice.MailService.MailService;

@Controller
public class MailSenderTestController {

    @Autowired
    MailService mailService;

    @GetMapping("/testmail")
    public String sendTestMail() {
        mailService.send("daref67649@3dmail.top","test","Lorem ipsum dolor sit amet");
        return "testmail";
    }
}
