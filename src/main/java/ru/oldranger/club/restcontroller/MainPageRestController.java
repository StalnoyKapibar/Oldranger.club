package ru.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.oldranger.club.dto.SectionsAndTopicsDto;
import ru.oldranger.club.service.forum.SectionsAndTopicsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Sections and topics")
public class MainPageRestController {

    private SectionsAndTopicsService sectionsAndTopicsService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get SectionsAndTopicsDto list",
               description = "Get available sections, limit topics in section: 10",
               tags = { "Sections and topics" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionsAndTopicsDto.class)))) })
    @GetMapping(value = "", produces = { "application/json" })
    public ResponseEntity<List<SectionsAndTopicsDto>> mainPage() {
        List<SectionsAndTopicsDto> dtos = sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection();
        return ResponseEntity.ok(dtos);
    }

}
