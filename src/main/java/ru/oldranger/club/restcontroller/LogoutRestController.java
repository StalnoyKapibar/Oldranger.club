package ru.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Logout")
public class LogoutRestController {


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Clear SecurityHolder", tags = {"Logout"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Ok! User is offline!"),
            @ApiResponse(responseCode = "204", description = "Bad logout")})
    @GetMapping("/logout")
    public ResponseEntity logout(){
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
