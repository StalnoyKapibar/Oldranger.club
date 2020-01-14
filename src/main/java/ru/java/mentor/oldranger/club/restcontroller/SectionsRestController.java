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
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sections")
@Tag(name = "Sections and subsections")
@AllArgsConstructor
public class SectionsRestController {

    private final SectionService sectionService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all Sections", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Section.class)))),
            @ApiResponse(responseCode = "204", description = "No sections")})
    @GetMapping(value = "/", produces = { "application/json" })
    public ResponseEntity<List<Section>> getAllSections() {
        List<Section> sections = sectionService.getAll();
        if (sections == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sections);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a section by Id", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Subsection.class))),
            @ApiResponse(responseCode = "204", description = "User is not logged in"),
            @ApiResponse(responseCode = "400", description = "Section doesn't exist")})
    @GetMapping(value = "/{sectionId}", produces = { "application/json" })
    public ResponseEntity<Section> getSectionById(@Parameter(description = "section's id", example = "1") @PathVariable Long sectionId) {

        Optional<Section> optionalSection = sectionService.findById(sectionId);

        if (!optionalSection.isPresent() ) {
            return ResponseEntity.badRequest().build();
        }
        Section section = optionalSection.get();

        if (securityUtilsService.getLoggedUser() == null && section.isHideToAnon()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(section);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all Sections for anon", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Section.class)))),
            @ApiResponse(responseCode = "204", description = "No sections for anon")})
    @GetMapping(value = "/anonsections", produces = { "application/json" })
    public ResponseEntity<List<Section>> getSection() {
        List<Section> sections = sectionService.getAllForAnon();
        if (sections == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sections);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update section's info", tags = {"Sections and subsections"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Section.class)))),
            @ApiResponse(responseCode = "204", description = "User is not logged in or hasn't got enough rights"),
            @ApiResponse(responseCode = "400", description = "Section name or position is already in use")
    })
    @PutMapping(value = "/updateSection", produces = { "application/json" })
    public ResponseEntity<Section> updateSection(@RequestBody Section newSection) {
        User user = securityUtilsService.getLoggedUser();
        boolean isAdmin = securityUtilsService.isLoggedUserHasRole(Role.ROLE_ADMIN);
        if (!isAdmin || user == null) {
            return ResponseEntity.noContent().build();
        }
        Optional<Section> optionalSection = sectionService.findById(newSection.getId());

        if (!optionalSection.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        Section oldSection = optionalSection.get();

        if (newSection.getName().equals(oldSection.getName()) || sectionService.countAllByName(newSection.getName()) == 0) {
            if (newSection.getPosition() == oldSection.getPosition() || sectionService.countAllByPosition(newSection.getPosition()) == 0) {
                return ResponseEntity.ok(sectionService.update(newSection));
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete Section ", description = "Delete section by id", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User hasn't got enough rights"),
            @ApiResponse(responseCode = "400", description = "Current section doesn't exist")})
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteSection(@Parameter(description = "section's id", example = "1") @PathVariable("id") String id) {


        User user = securityUtilsService.getLoggedUser();
        boolean isAdmin = securityUtilsService.isLoggedUserHasRole(Role.ROLE_ADMIN);

        if (!isAdmin || user == null) {
            return ResponseEntity.noContent().build();
        }

        Optional<Section> optionalSection = sectionService.findById(Long.parseLong(id));

        if (!optionalSection.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Section section = optionalSection.get();
        sectionService.delete(section);
        return ResponseEntity.ok("section was deleted");
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add Section ", description = "Add new section", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User hasn't got enough rights"),
            @ApiResponse(responseCode = "400", description = "Section's name or position is already in use")})
    @PostMapping(value = "/savesection")
    public ResponseEntity<String> changePassword(@Parameter(description = "Section name")
                                                   @RequestParam(value = "name") String name,
                                                   @Parameter(description = "Sections's position")
                                                   @RequestParam(value = "position") Integer position,
                                                   @Parameter(description = "Is hide to anon")
                                                   @RequestParam(value = "isHideToAnon") boolean isHideToAnon) {

        User user = securityUtilsService.getLoggedUser();
        boolean isAdmin = securityUtilsService.isLoggedUserHasRole(Role.ROLE_ADMIN);

        if (!isAdmin || user == null) {
            return ResponseEntity.noContent().build();
        }

        if (sectionService.countAllByName(name) > 0 || sectionService.countAllByPosition(position) > 0) {
            return ResponseEntity.badRequest().build();

        }
        Section section = new Section(name, position, isHideToAnon);
        sectionService.addSection(section);
        return ResponseEntity.ok("section was added");
    }

}
