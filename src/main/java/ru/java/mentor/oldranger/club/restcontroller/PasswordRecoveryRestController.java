package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.dto.PasswordRecoveryDto;
import ru.java.mentor.oldranger.club.dto.PasswordRecoveryStatusDto;
import ru.java.mentor.oldranger.club.dto.RecoveryTokenDto;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.PasswordsService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class PasswordRecoveryRestController {

    private UserService userService;

    private PasswordsService passwordsService;

    private PasswordRecoveryService passwordRecoveryService;

    @GetMapping("/passwordrecovery")
    public String requestForPasswordRecovery() {
        return "passwordrecovery/request";
    }

    @PostMapping("/passwordrecovery")
    public ResponseEntity<PasswordRecoveryStatusDto> requestForPasswordRecovery(@RequestParam(name = "email") String email) {
        User user = userService.getUserByEmail(email);
        String recoveryStatus;
        Object next_recovery_possible = null;
        if (user != null) {
            try {
                passwordRecoveryService.sendRecoveryTokenToEmail(user);
                recoveryStatus = "ok_mail_sent";
            } catch (PasswordRecoveryIntervalViolation passwordRecoveryIntervalViolation) {
                recoveryStatus = "fail_invalid_interval";
                next_recovery_possible = passwordRecoveryIntervalViolation.getNextPossibleRecoveryTime();
            }
        } else {
            recoveryStatus = "fail_bad_credentials";
        }
        return ResponseEntity.ok(new PasswordRecoveryStatusDto(recoveryStatus, next_recovery_possible));
    }

    @GetMapping("/token/{recoveryToken}")
    public ResponseEntity<RecoveryTokenDto> responseOnRecoveryToken(@PathVariable String recoveryToken) {
        String token = null;
        String tokenstatus = null;
        try {
            passwordRecoveryService.validateToken(recoveryToken);
            token = recoveryToken;
            tokenstatus = "ok_valid";
        } catch (PasswordRecoveryTokenExpired e) {
            tokenstatus = "fail_expired";
        } catch (PasswordRecoveryInvalidToken e) {
            tokenstatus = "fail_invalid";
        }
        return ResponseEntity.ok(new RecoveryTokenDto(token, tokenstatus));
    }

    @PostMapping("/setnewpassword")
    public ResponseEntity<PasswordRecoveryDto> responseOnRecoveryTokenAndNewPassword(@RequestParam(name = "token") String recoveryToken,
                                                                                     @RequestParam(name = "password") String password,
                                                                                     @RequestParam(name = "password_confirm") String passwordConfirm) {
        String token = recoveryToken;
        String tokenStatus;
        String passwordStatus = null;
        try {
            PasswordRecoveryToken passwordRecoveryToken = passwordRecoveryService.validateToken(recoveryToken);
            tokenStatus = "ok_valid";
            try {
                passwordsService.checkStrength(password);
                if (!password.equals(passwordConfirm)) {
                    passwordStatus = "fail_mismatch";
                } else {
                    passwordRecoveryService.updatePassword(passwordRecoveryToken, password);
                    passwordStatus = "ok_changed";
                }
            } catch (Exception e) {
                passwordStatus = "fail_wrong_type";
            }
        } catch (PasswordRecoveryTokenExpired e) {
            tokenStatus = "fail_expired";
        } catch (PasswordRecoveryInvalidToken e) {
            tokenStatus = "fail_invalid";
        }
        return ResponseEntity.ok(new PasswordRecoveryDto(token, tokenStatus, passwordStatus));
    }
}
