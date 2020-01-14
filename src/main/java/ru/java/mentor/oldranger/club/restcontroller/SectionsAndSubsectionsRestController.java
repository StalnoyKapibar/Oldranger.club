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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Sections and subsections")
public class SectionsAndSubsectionsRestController {

    private SectionsAndSubsectionsService sectionsAndSubsectionsService;
    private SubsectionService subsectionService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get all SectionsAndSubsectionsDto", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionsAndSubsectionsDto.class)))) })
    @GetMapping(value = "/allsectionsandsubsections", produces = { "application/json" })
    public ResponseEntity<List<SectionsAndSubsectionsDto>> getSectionsAndSubsectionsDto() {
        List<SectionsAndSubsectionsDto> dtos = sectionsAndSubsectionsService.getAllSectionsAndSubsections();
        if (dtos == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Swap sections", tags = { "Sections and subsections" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200") })
    @PatchMapping(value = "/swapsections", produces = { "application/json" })
    public ResponseEntity swapSections(@Parameter(description = "Sections id ", example = "[1]") @RequestBody List<Long> sectionsId) {
        sectionsAndSubsectionsService.swapSections(sectionsId);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Swap subsections", tags = { "Sections and subsections" })
    @ApiResponses(value = { @ApiResponse(responseCode = "200") })
    @PatchMapping(value = "/swapsubsections", produces = { "application/json" })
    public ResponseEntity swapSubsections(@Parameter(description = "Each section contains several subsections (sectionId:subsectionIdList)")  @RequestBody Map<Long, List<String>> sectionsAndSubsectionsIds) {
        sectionsAndSubsectionsService.swapSubsections(sectionsAndSubsectionsIds);
        return ResponseEntity.ok().build();
    }
}
