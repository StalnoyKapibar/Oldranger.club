package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminRestController {

    UserStatisticService userStatisticService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get UserStatisticDto list", tags = { "Admin" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserStatisticDto.class)))) })
    @GetMapping(value = "/users", produces = { "application/json" })
    public ResponseEntity<List<UserStatisticDto>> getAllUsers(@RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "query", required = false) String query) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 5, Sort.by("user_id"));

        List<UserStatistic> users = userStatisticService.getAllUserStatistic(pageable).getContent();

        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim().replaceAll("\\s++", ",");
            users = userStatisticService.getUserStatisticsByQuery(pageable, query).getContent();
        }

        List<UserStatisticDto> dtos = userStatisticService.getUserStatisticDtoFromUserStatistic(users);
        return ResponseEntity.ok(dtos);
    }

}
