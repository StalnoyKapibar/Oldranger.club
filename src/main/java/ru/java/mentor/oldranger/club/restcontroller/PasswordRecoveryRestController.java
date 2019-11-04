package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryNoSuchUser;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class PasswordRecoveryRestController {

    private UserService userService;

    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/recoverpassword")
    public ResponseEntity<String> requestForPasswordRecovery(@RequestParam(name = "username") String username,
                                                             @RequestParam(name = "email") String email) {
        User user = userService.getUserByNickName(username);
        if (user != null && user.getEmail().equals(email)) {
            passwordRecoveryService.sendRecoveryTokenToEmail(user);
            return ResponseEntity.ok("Check email");
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/recoverpassword/{recoveryToken}")
    public ResponseEntity<String> responseOnRecoveryToken(@PathVariable String recoveryToken) {
        try {
            passwordRecoveryService.updatePasswordForUserWithToken(recoveryToken);
            return ResponseEntity.ok("New password e-mailed to you");
        } catch (PasswordRecoveryTokenExpired e) {
            return ResponseEntity.ok("Expired token");
        } catch (PasswordRecoveryNoSuchUser e) {
            return ResponseEntity.ok("No such user");
        } catch (PasswordRecoveryInvalidToken e) {
            return ResponseEntity.ok("Invalid token");
        }
    }
}
