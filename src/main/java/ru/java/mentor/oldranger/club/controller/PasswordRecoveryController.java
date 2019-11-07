package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
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
    public ModelAndView requestForPasswordRecovery(@RequestParam(name = "email") String email) {
        ModelAndView modelAndView = new ModelAndView("passwordrecovery/request");
        User user = userService.getUserByEmail(email);
        if (user != null) {
            try {
                passwordRecoveryService.sendRecoveryTokenToEmail(user);
                modelAndView.addObject("recoverystatus", "ok_mail_sent");
            } catch (PasswordRecoveryIntervalViolation passwordRecoveryIntervalViolation) {
                modelAndView.addObject("recoverystatus", "fail_invalid_interval");
                modelAndView.addObject("next_recovery_possible", passwordRecoveryIntervalViolation.getNextPossibleRecoveryTime());
            }
        } else {
            modelAndView.addObject("recoverystatus", "fail_bad_credentials");
        }
        return modelAndView;
    }

    @GetMapping("/token/{recoveryToken}")
    public ModelAndView responseOnRecoveryToken(@PathVariable String recoveryToken) {
        ModelAndView modelAndView = new ModelAndView("passwordrecovery/tokenhandler");
        try {
            passwordRecoveryService.validateToken(recoveryToken);
            modelAndView.addObject("token", recoveryToken);
            modelAndView.addObject("tokenstatus", "ok_valid");
        } catch (PasswordRecoveryTokenExpired e) {
            modelAndView.addObject("tokenstatus", "fail_expired");
        } catch (PasswordRecoveryInvalidToken e) {
            modelAndView.addObject("tokenstatus", "fail_invalid");
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
            modelAndView.addObject("tokenstatus", "ok_valid");
            try {
                passwordsService.checkStrength(password);
                if (!password.equals(passwordConfirm)) {
                    modelAndView.addObject("passwordstatus", "fail_mismatch");
                } else {
                    passwordRecoveryService.updatePassword(passwordRecoveryToken, password);
                    modelAndView.addObject("passwordstatus", "ok_changed");
                }
            } catch (Exception e) {
                modelAndView.addObject("passwordstatus", "fail_wrong_type");
            }
        } catch (PasswordRecoveryTokenExpired e) {
            modelAndView.addObject("tokenstatus", "fail_expired");
        } catch (PasswordRecoveryInvalidToken e) {
            modelAndView.addObject("tokenstatus", "fail_invalid");
        }
        return modelAndView;
    }
}
