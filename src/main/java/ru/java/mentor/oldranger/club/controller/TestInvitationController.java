package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.service.user.InvitationService;

@Hidden
@Controller
@RequestMapping("/invite")
public class TestInvitationController {
    private InvitationService invitationService;

    @Autowired
    public void setInvitationService(InvitationService service) {
        this.invitationService = service;
    }

    @GetMapping
    public ModelAndView registrationPage(String key) {
        InvitationToken invitationToken = invitationService.getInvitationTokenByKey(key);
        ModelAndView modelAndView = new ModelAndView();
        if (invitationToken == null || invitationService.checkShelfLife(invitationToken) || invitationToken.getUsed()) {
            modelAndView.setViewName("redirect:/");
        } else {
            modelAndView.setViewName("registration");
        }
        return modelAndView;
    }
}
