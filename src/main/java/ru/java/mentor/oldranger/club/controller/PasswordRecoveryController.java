package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.PasswordsService;

@Controller
@RequestMapping("/passwordrecovery")
@AllArgsConstructor
public class PasswordRecoveryController {

    private UserService userService;

    private PasswordsService passwordsService;

    private PasswordRecoveryService passwordRecoveryService;

    @GetMapping
    public String requestForPasswordRecovery() {
        return "passwordrecovery/request";
    }

    @PostMapping
    public ModelAndView requestForPasswordRecovery(@RequestParam(name = "username") String username,
                                                   @RequestParam(name = "email") String email) {
        ModelAndView modelAndView = new ModelAndView("passwordrecovery/request");
        User user = userService.getUserByNickName(username);
        if (user != null && user.getEmail().equals(email)) {
            passwordRecoveryService.sendRecoveryTokenToEmail(user);
            modelAndView.addObject("recoverystatus", true);
        } else {
            modelAndView.addObject("recoverystatus", false);
        }
        return modelAndView;
    }

    @GetMapping("/token/{recoveryToken}")
    public ModelAndView responseOnRecoveryToken(@PathVariable String recoveryToken) {
        ModelAndView modelAndView = new ModelAndView("passwordrecovery/tokenhandler");
        try {
            passwordRecoveryService.validateToken(recoveryToken);
            modelAndView.addObject("token", recoveryToken);
            modelAndView.addObject("tokenstatus", "ok");
        } catch (PasswordRecoveryTokenExpired e) {
            modelAndView.addObject("tokenstatus", "expired");
        } catch (PasswordRecoveryInvalidToken e) {
            modelAndView.addObject("tokenstatus", "invalid");
        }
        return modelAndView;
    }

    @PostMapping("/setnewpassword")
    public ModelAndView responseOnRecoveryTokenAndNewPassword(@RequestParam(name = "token") String recoveryToken,
                                                              @RequestParam(name = "password") String password,
                                                              @RequestParam(name = "password_confirm") String passwordConfirm) {
        ModelAndView modelAndView = new ModelAndView("passwordrecovery/tokenhandler");
        modelAndView.addObject("token", recoveryToken);
        try {
            PasswordRecoveryToken passwordRecoveryToken = passwordRecoveryService.validateToken(recoveryToken);
            modelAndView.addObject("tokenstatus", "ok");
            try {
                passwordsService.checkStrength(password);
                if (!password.equals(passwordConfirm)) {
                    modelAndView.addObject("passwordstatus", "mismatch");
                } else {
                    passwordRecoveryService.updatePassword(passwordRecoveryToken, password);
                    modelAndView.addObject("passwordstatus", "changed");
                }
            } catch (Exception e) {
                modelAndView.addObject("passwordstatus", "wrongtype");
            }
        } catch (PasswordRecoveryTokenExpired e) {
            modelAndView.addObject("tokenstatus", "expired");
        } catch (PasswordRecoveryInvalidToken e) {
            modelAndView.addObject("tokenstatus", "invalid");
        }
        return modelAndView;
    }
}
