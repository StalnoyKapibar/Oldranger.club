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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.SectionDto;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.dto.SubsectionDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Sections and subsections")
public class SectionsAndSubsectionsRestController {

    private SectionsAndSubsectionsService sectionsAndSubsectionsService;
    private SecurityUtilsService securityUtilsService;
    private SectionService sectionService;
    private SubsectionService subsectionService;

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
            summary = "Create a new section", tags = {"Sections and subsections"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Section successfully saved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionDto.class)))),
            @ApiResponse(responseCode = "406", description = "Section already exists with such name"),
            @ApiResponse(responseCode = "401", description = "User have no authority")})
    @PostMapping(value = "/section/add", produces = {"application/json"})
    public ResponseEntity<SectionDto> addSection(@RequestParam String name,
                                                 @RequestParam int position,
                                                 @RequestParam boolean isHiddenToAnon) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null || !securityUtilsService.isLoggedUserIsUser()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Section> sectionsWithEqualsName = sectionService.findSectionsByName(name);
        if (!sectionsWithEqualsName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        if (position == 0) {
            int maxPosition = sectionService.findMaxPosition();
            position = maxPosition + 1;
            SectionDto dto = sectionService.addSection(new Section(name, position, isHiddenToAnon));
            return ResponseEntity.ok(dto);
        }
        sectionService.updateSectionsPosition(position);
        SectionDto dto = sectionService.addSection(new Section(name, position, isHiddenToAnon));
        return ResponseEntity.ok(dto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Create a new subsection", tags = {"Sections and subsections"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subsection successfully saved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionDto.class)))),
            @ApiResponse(responseCode = "406", description = "Subsection already exists with such name"),
            @ApiResponse(responseCode = "401", description = "User have no authority")})
    @PostMapping(value = "/subsection/add", produces = {"application/json"})
    public ResponseEntity<SubsectionDto> addSubSection(@RequestParam Long sectionId,
                                                       @RequestParam String name,
                                                       @RequestParam int position,
                                                       @RequestParam boolean isHiddenToAnon) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null || !securityUtilsService.isLoggedUserIsUser()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Subsection> subsectionWithEqualsName = subsectionService.findSubsectionsByName(name);
        if (!subsectionWithEqualsName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        Section section = sectionService.getById(sectionId).get();
        if (position == 0) {
            int maxPosition = subsectionService.findMaxPosition();
            position = maxPosition + 1;
            SubsectionDto dto = subsectionService.createSubsection(new Subsection(name, position, section, isHiddenToAnon));
            return ResponseEntity.ok(dto);
        }
        subsectionService.updateSubsectionsPosition(position);
        SubsectionDto dto = subsectionService.createSubsection(new Subsection(name, position, section, isHiddenToAnon));
        return ResponseEntity.ok(dto);
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
