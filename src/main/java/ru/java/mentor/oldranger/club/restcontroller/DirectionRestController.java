package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.model.mail.Direction;
import ru.java.mentor.oldranger.club.model.mail.DirectionType;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailDirectionService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "Mail direction API")
@RequestMapping("/api")
public class DirectionRestController {
    MailDirectionService mailDirectionService;
    SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Set mail direction for user", tags = {"Mail direction API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = DirectionType.class)), description = "ONE_TO_DAY, TWO_TO_DAY, ONE_TO_WEEK, NEVER"),
            @ApiResponse(responseCode = "401", description = "User not found")})
    @PostMapping(value = "/changeDirection", produces = {"application/json"})
    public ResponseEntity<String> setDirection(@Parameter(description = "Тип подписки")
                                               @RequestParam(value = "directionType") String directionType) {
        DirectionType type = Direction.stringToDirectionType(directionType);
        User user = securityUtilsService.getLoggedUser();
        try {
            mailDirectionService.changeUserDirection(user, type);
            return ResponseEntity.ok("Direction change is OK!");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
