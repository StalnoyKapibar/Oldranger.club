package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserAvatarService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/avatar")
@Tag(name = "Avatar from user")
public class AvatarUserRestController {

    private UserAvatarService userAvatarService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Set Avatar ", description = "Set avatar to user ", tags = {"Avatar from user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PostMapping(value = "/set", produces = {"application/json"})
    public ResponseEntity<String> setAvatarToUser(@RequestParam("file") MultipartFile file) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (user.getAvatar() == null) {
                userAvatarService.setAvatarToUser(user, file);
            } else {
                userAvatarService.updateUserAvatar(user, file);
            }
        } catch (Exception o) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok("аватар загружен");
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete Avatar ", description = "Delete avatar", tags = {"Avatar from user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PostMapping(value = "/delete", produces = {"application/json"})
    public ResponseEntity<String> setAvatarToUser() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (user.getAvatar() != null) {
                userAvatarService.deleteUserAvatar(user);
            } else {
                ResponseEntity.noContent().build();
            }
        } catch (Exception o) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok("удален");
    }
}
