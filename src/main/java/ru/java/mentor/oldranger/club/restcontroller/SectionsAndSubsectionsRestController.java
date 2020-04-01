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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Sections and subsections")
public class SectionsAndSubsectionsRestController {

    private SectionsAndSubsectionsService sectionsAndSubsectionsService;
    private SecurityUtilsService securityUtilsService;
    private SectionService sectionService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all SectionsAndSubsectionsDto", tags = {"Sections and subsections"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionsAndSubsectionsDto.class))))})
    @GetMapping(value = "/allsectionsandsubsections", produces = {"application/json"})
    public ResponseEntity<List<SectionsAndSubsectionsDto>> getSectionsAndSubsectionsDto() {
        List<SectionsAndSubsectionsDto> dtos = null;
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            dtos = sectionsAndSubsectionsService.getAllSectionsAndSubsectionsForAnon();
        } else {
            dtos = sectionsAndSubsectionsService.getAllSectionsAndSubsections();
        }
        if (dtos == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Swap sections", tags = {"Sections and subsections"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200")})
    @PatchMapping(value = "/swapsections", produces = {"application/json"})
    public ResponseEntity swapSections(@RequestBody List<Long> sectionsId) {
        sectionsAndSubsectionsService.swapSections(sectionsId);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Swap subsections", tags = {"Sections and subsections"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200")})
    @PatchMapping(value = "/swapsubsections", produces = {"application/json"})
    public ResponseEntity swapSubsections(@RequestBody Map<Long, List<String>> sectionsAndSubsectionsIds) {
        sectionsAndSubsectionsService.swapSubsectons(sectionsAndSubsectionsIds);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a subsection by Id", tags = {"Sections and subsections"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Subsection.class)))})
    @GetMapping(value = "/getsubsection/{subsectionId}", produces = {"application/json"})
    public ResponseEntity<Subsection> getSubsectionById(@PathVariable Long subsectionId) {
        Subsection subsection = sectionsAndSubsectionsService.getSubsectionById(subsectionId);
        if (subsection == null && subsection.getId() == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subsection);
    }

}
