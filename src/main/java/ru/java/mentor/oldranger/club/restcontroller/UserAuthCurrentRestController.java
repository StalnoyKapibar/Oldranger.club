package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.*;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserAuthCurrentRestController {
    private UserService userService;
    private SecurityUtilsService securityUtilsService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get UserAuthDto of logged in user", tags = {"User current"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserAuthDTO.class))),
            @ApiResponse(responseCode = "204", description = "User is not logged in")})
    @GetMapping(value = "/currentUser", produces = {"application/json"})
    public ResponseEntity<UserAuthDTO> getCurrentUser() {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();
        UserAuthDTO dto = userService.buildUserDtoByUser(currentUser, securityUtilsService.isLoggedUserIsUser());
        return ResponseEntity.ok(dto);
    }
}
