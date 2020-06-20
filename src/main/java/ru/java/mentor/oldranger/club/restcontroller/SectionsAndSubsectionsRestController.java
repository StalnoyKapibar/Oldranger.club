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
import java.util.stream.Collectors;

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
        List<Section> sections = sectionService.getAllSections();
        List<Section> sectionsWithEqualsName = sections.stream().filter(section -> section.getName()
                .equals(name)).collect(Collectors.toList());
        if (!sectionsWithEqualsName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        if (position == 0) {
            Optional<Integer> maxPosition = sections.stream().map(Section::getPosition).max(Comparator.naturalOrder());
            position = maxPosition.get() + 1;
            Section section = sectionService.addSection(new Section(name, position, isHiddenToAnon));
            SectionDto dto = new SectionDto(section.getId(), name, position);
            return ResponseEntity.ok(dto);
        }
        List<Long> sectionsIdWithEqualsPosition = new ArrayList<>();
        int finalPosition = position;
        sections.stream().filter(section -> section.getPosition() >= finalPosition)
                .sorted(Comparator.comparing(Section::getPosition))
                .forEach(section -> sectionsIdWithEqualsPosition.add(section.getId()));
        if (!sectionsIdWithEqualsPosition.isEmpty()) {
            sectionsAndSubsectionsService.moveSectionsByIds(sectionsIdWithEqualsPosition);
        }
        Section section = sectionService.addSection(new Section(name, finalPosition, isHiddenToAnon));
        SectionDto dto = new SectionDto(section.getId(), name, finalPosition);
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
        List<Subsection> subsectionWithEqualsName = new ArrayList<>();
        subsectionService.getAllSubsections().stream()
                .filter(subsection -> subsection.getName().equals(name)).forEach(subsectionWithEqualsName::add);
        if (!subsectionWithEqualsName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        Section section = sectionService.getById(sectionId).get();
        if (position == 0) {
            List<Subsection> subsections = subsectionService.getAllSubsections();
            Optional<Integer> maxPosition = subsections.stream()
                    .filter(subsection -> subsection.getSection().getId().equals(sectionId))
                    .map(Subsection::getPosition).max(Comparator.naturalOrder());
            position = maxPosition.get() + 1;
            subsectionService.createSubsection(new Subsection(name, position, section, isHiddenToAnon));
            SubsectionDto dto = new SubsectionDto(section, name, position);
            return ResponseEntity.ok(dto);
        }
        List<Long> idList = new ArrayList<>();
        int finalPosition = position;
        subsectionService.getAllSubsections().stream().filter(subsection -> subsection.getPosition() >= finalPosition)
                .filter(subsection -> subsection.getSection().equals(section))
                .sorted(Comparator.comparing(Subsection::getPosition))
                .forEach(subsection -> idList.add(subsection.getId()));
        if (!idList.isEmpty()) {
            sectionsAndSubsectionsService.moveSubsectionsByIds(idList);
        }
        Subsection subsection = new Subsection(name, finalPosition, section, isHiddenToAnon);
        subsectionService.createSubsection(subsection);
        SubsectionDto dto = new SubsectionDto(section, name, finalPosition);
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
