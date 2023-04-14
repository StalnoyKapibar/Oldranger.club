package ru.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldranger.club.dto.PasswordRecoveryDto;
import ru.oldranger.club.dto.PasswordRecoveryStatusDto;
import ru.oldranger.club.dto.RecoveryTokenDto;
import ru.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
import ru.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.oldranger.club.model.user.PasswordRecoveryToken;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.user.PasswordRecoveryService;
import ru.oldranger.club.service.user.UserService;
import ru.oldranger.club.service.utils.PasswordsService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Password recovery")
public class PasswordRecoveryRestController {

    private UserService userService;
    private PasswordsService passwordsService;
    private PasswordRecoveryService passwordRecoveryService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Request for password recovery", tags = { "Password recovery" })
    @PostMapping(value = "/passwordrecovery", produces = { "application/json" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PasswordRecoveryStatusDto",
                    content = @Content(schema = @Schema(implementation = PasswordRecoveryStatusDto.class))) })
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

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Response on recovery token", description = "Return RecoveryTokenDto", tags = { "Password recovery" })
    @GetMapping(value = "/token/{recoveryToken}", produces = { "application/json" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RecoveryTokenDto",
                    content = @Content(schema = @Schema(implementation = RecoveryTokenDto.class))) })
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

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Response on recovery token and new password", description = "Return PasswordRecoveryDto", tags = { "Password recovery" })
    @PostMapping(value = "/setnewpassword", produces = { "application/json" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PasswordRecoveryDto",
                    content = @Content(schema = @Schema(implementation = PasswordRecoveryDto.class))) })
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
