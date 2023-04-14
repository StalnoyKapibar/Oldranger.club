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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.cach.CacheService;
import ru.oldranger.club.service.utils.SecurityUtilsService;

@AllArgsConstructor
@RestController
@RequestMapping("api/cache")
@Tag(name = "Clear cache")
public class CacheRestController {
    private CacheService cacheService;
    private SecurityUtilsService securityUtilsService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Clear cache ", description = "Принудительная отчистка кеша", tags = { "Clear cache" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class)))})
    @DeleteMapping
    public ResponseEntity<String> clearAllCache() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.noContent().build();
        }
        try {
            cacheService.clearAllCaches();
        } catch (Exception e) {
        }
        return ResponseEntity.ok().build();
    }
}
