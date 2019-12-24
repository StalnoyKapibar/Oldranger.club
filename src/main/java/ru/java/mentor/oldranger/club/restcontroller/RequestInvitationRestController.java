package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.RequestInvitation;
import ru.java.mentor.oldranger.club.service.user.RequestInvitationService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/requestInvite")
@Tag(name = "Request Invitation")
public class RequestInvitationRestController {
    private RequestInvitationService requestInvitationService;
    private SecurityUtilsService securityUtilsService;
    private RoleService roleService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all requests on invite from guests", tags = { "Request Invitation" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RequestInvitation.class)))),
            @ApiResponse(responseCode = "403", description = "User does not have enough rights")})
    @GetMapping(produces = { "application/json" })
    public ResponseEntity<List<RequestInvitation>> getRequestInvitation() {

        if (!securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_ADMIN"))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(requestInvitationService.findAll());
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "delete request invitation by id", tags = { "Request Invitation" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request on invitation has been successfully deleted",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "User does not have enough rights or User doesn't exist")})
    @DeleteMapping(value = "/delete", produces = { "application/json" })
    public ResponseEntity<String> delRequestInvitation(@Parameter(description = "id of request invitation") @RequestParam Long id) {
        if (!securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_ADMIN")) || id == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        requestInvitationService.deleteById(id);
        return ResponseEntity.ok("Ok");
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "send request invitation", tags = { "Request Invitation" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request on invitation was send successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "423", description = "User already logged in")})
    @PostMapping(value = "/send", produces = { "application/json" })
    public ResponseEntity<String> saveRequestInvitation(@Parameter(description = "request invitation") @RequestBody RequestInvitation requestInvitation) {
        if (!securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_PROSPECT"))) {
            requestInvitationService.save(requestInvitation);
            return ResponseEntity.ok("Ok");
        } else {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
    }
}
