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
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sections")
@Tag(name = "Sections and subsections")
@AllArgsConstructor
public class SubsectionRestController {

    private final SubsectionService subsectionService;
    private final SectionService sectionService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all Subsections", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Subsection.class)))),
            @ApiResponse(responseCode = "204", description = "No subsections")})
    @GetMapping(value = "/subsections", produces = { "application/json" })
    public ResponseEntity<List<Subsection>> getAllSubsections() {
        List<Subsection> subSections = subsectionService.getAllSubsections();
        if (subSections == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(subSections);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a subsection by Id", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Subsection.class))),
            @ApiResponse(responseCode = "204", description = "Current subsection is unreachable for anon users"),
            @ApiResponse(responseCode = "400", description = "Subsection doesn't exist")})
    @GetMapping(value = "/subsections/{subsectionId}", produces = { "application/json" })
    public ResponseEntity<Subsection> getSubsectionById(@Parameter(description = "subsection's id", example = "1") @PathVariable Long subsectionId) {

        Optional<Subsection> optionalSubsection = subsectionService.getById(subsectionId);

        if (!optionalSubsection.isPresent() ) {
            return ResponseEntity.badRequest().build();
        }
        Subsection subsection = optionalSubsection.get();

        if (securityUtilsService.getLoggedUser() == null && subsection.isHideToAnon()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subsection);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all Subsections for anonymous", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Subsection.class)))),
            @ApiResponse(responseCode = "204", description = "No subsections for anon users")})
    @GetMapping(value = "/anonsubsections", produces = { "application/json" })
    public ResponseEntity<List<Subsection>> getAnonSubsections() {
        List<Subsection> subsections = subsectionService.getAllSubsectionsForAnon();
        if (subsections == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subsections);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all Subsections of current section for anon users", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Subsection.class)))),
            @ApiResponse(responseCode = "204", description = "No subsections for anon users"),
            @ApiResponse(responseCode = "400", description = "Section doesn't exist")})
    @GetMapping(value = "/{sectionId}/anonsubsections", produces = { "application/json" })
    public ResponseEntity<List<Subsection>> getAnonSubsectionsOfSection(@Parameter(description = "subsection's id", example = "1") @PathVariable Long sectionId) {
        Optional<Section> optionalSection = sectionService.findById(sectionId);

        if (!optionalSection.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        List<Subsection> subsections = subsectionService.getAllSubsectionsForAnonBySection(sectionService.findById(sectionId).get());

        if (subsections == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subsections);
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all Subsections for current section", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Subsection.class)))),
            @ApiResponse(responseCode = "204", description = "No subsections for current section"),
            @ApiResponse(responseCode = "400", description = "Section doesn't exist")})
    @GetMapping(value = "/{sectionId}/subsections", produces = { "application/json" })
    public ResponseEntity<List<Subsection>> getSubsection(@Parameter(description = "section's id", example = "1") @PathVariable Long sectionId) {
        Optional<Section> optionalSection = sectionService.findById(sectionId);

        if (!optionalSection.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Section section = sectionService.findById(sectionId).get();
        List<Subsection> subsections = subsectionService.getAllSubsectionsBySection(section);

        if (subsections == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(subsections);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update subsection's info", tags = {"Sections and subsections"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Section.class)))),
            @ApiResponse(responseCode = "204", description = "User is not logged in or hasn't got enough rights"),
            @ApiResponse(responseCode = "400", description = "Subsection name or position is already in use")
    })
    @PutMapping(value = "/updateSubsection", produces = { "application/json" })
    public ResponseEntity<Subsection> updateSection(@RequestBody Subsection newSubsection) {
        User user = securityUtilsService.getLoggedUser();
        boolean isAdmin = securityUtilsService.isLoggedUserHasRole(Role.ROLE_ADMIN);
        if (!isAdmin || user == null) {
            return ResponseEntity.noContent().build();
        }
        Optional<Subsection> optionalSubsection = subsectionService.getById(newSubsection.getId());

        if (!optionalSubsection.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        Subsection oldSubsection = optionalSubsection.get();

            if (newSubsection.getPosition() == oldSubsection.getPosition() || subsectionService.countAllByPosition(newSubsection.getPosition()) == 0) {
                return ResponseEntity.ok(subsectionService.updateSubsection(newSubsection));
            }
            return ResponseEntity.badRequest().build();
        }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete Subsection ", description = "Delete subsection by id", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User hasn't got enough rights"),
            @ApiResponse(responseCode = "400", description = "Subsection doesn't exist")})
    @DeleteMapping(value = {"/subsections/delete/{subsectionId}"}, produces = { "application/json" })
    public ResponseEntity<String> deleteSubsection(@Parameter(description = "subsection's id", example = "1") @PathVariable("subsectionId") String id) {
        User user = securityUtilsService.getLoggedUser();
        boolean isAdmin = securityUtilsService.isLoggedUserHasRole(Role.ROLE_ADMIN);

        if (!isAdmin || user == null) {
            return ResponseEntity.noContent().build();
        }

        Optional<Subsection> optionalSubsection = subsectionService.getById(Long.parseLong(id));

        if (!optionalSubsection.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Subsection subsection = optionalSubsection.get();
        subsectionService.deleteSubsection(subsection);
        return ResponseEntity.ok("subsection was deleted");
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add Subsection ", description = "Add new subsection", tags = { "Sections and subsections" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User hasn't got enough rights"),
            @ApiResponse(responseCode = "400", description = "Subsection's name or position is already in use")})
    @PostMapping(value = "/{sectionId}/savesubsection")
    public ResponseEntity<String> changePassword(@Parameter(description = "Section id")
                                                 @PathVariable(value = "sectionId") Long sectionId,
                                                 @Parameter(description = "Subsection name")
                                                 @RequestParam(value = "name") String name,
                                                 @Parameter(description = "Subsections's position")
                                                 @RequestParam(value = "position") Integer position,
                                                 @Parameter(description = "Is hide to anon")
                                                 @RequestParam(value = "isHideToAnon") boolean isHideToAnon) {

        User user = securityUtilsService.getLoggedUser();
        boolean isAdmin = securityUtilsService.isLoggedUserHasRole(Role.ROLE_ADMIN);

        if (!isAdmin || user == null) {
            return ResponseEntity.noContent().build();
        }

        if (subsectionService.countAllByName(name) > 0 || subsectionService.countAllByPosition(position) > 0) {
            return ResponseEntity.badRequest().build();

        }
        Subsection subsection = new Subsection(name, position, sectionService.findById(sectionId).get(), isHideToAnon);
        subsectionService.add(subsection);
        return ResponseEntity.ok("subsection was added");
    }

}
