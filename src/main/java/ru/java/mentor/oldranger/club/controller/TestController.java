package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.upload.UploadImageService;

import java.io.IOException;

@Controller
@RequestMapping("/test*")
public class TestController {

    @Autowired
    MailService mailService;

    @Autowired
    UploadImageService uploadImageService;

    // Пример работы с отправлением почты
    @GetMapping("/mail")
    @ResponseBody
    public String sendTestMail() {
        mailService.send("daref67649@3dmail.top","test","Lorem ipsum dolor sit amet");
        return "Mail send successfully";
    }

    @GetMapping("/profile")
    public String getTestProfile(Model model){
        User user = new User("User", "User", "user@javamentor.com", "User", new Role("ROLE_USER"));
        model.addAttribute("user",user);
        return "profile";
    }


    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        try {
            uploadImageService.uploadImage(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message", "Файл " + file.getOriginalFilename() + " успешно загружен!");

        return "redirect:/test/profile";
    }

}
