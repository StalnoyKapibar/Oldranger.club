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
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/articleTag")
@Tag(name = "Article Tag")
public class ArticleTagRestController {

    private ArticleTagService articleTagService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all article tags", tags = { "Article Tag" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTag.class)))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @GetMapping(value = "", produces = { "application/json" })
    public ResponseEntity<List<ArticleTag>> getTag() {
        if (!securityUtilsService.isAuthorityReachableForLoggedUser(new Role("ROLE_ADMIN")))
            return ResponseEntity.noContent().build();

        List<ArticleTag> tags = articleTagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Create new tag", tags = { "Article Tag" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTag.class))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @PostMapping(value = "", consumes = { "application/json" })
    public ResponseEntity<ArticleTag> createTag(@Parameter(description = "Name of a tag being created", required = true) @RequestParam String tagName) {
        if (!securityUtilsService.isAuthorityReachableForLoggedUser(new Role("ROLE_ADMIN")))
            return ResponseEntity.noContent().build();

        ArticleTag articleTag = new ArticleTag(tagName);
        articleTagService.addTag(articleTag);
        return ResponseEntity.ok(articleTag);
    }

    @Operation(security = @SecurityRequirement(name = "security", scopes = "ROLE_ADMIN"),
            summary = "Edit tag", tags = { "Article Tag" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTag.class))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @PutMapping(value = "/{tag_id}", consumes = { "application/json" })
    public ResponseEntity<ArticleTag> updateTag(@Parameter(description = "Id of a tag being updated", required = true) @PathVariable long tag_id,
                                                @Parameter(description = "Name of a tag being updated", required = true) @RequestParam String tagName) {
        if (!securityUtilsService.isAuthorityReachableForLoggedUser(new Role("ROLE_ADMIN")))
            return ResponseEntity.noContent().build();

        ArticleTag articleTag = new ArticleTag(tagName);
        articleTag.setId(tag_id);
        articleTagService.updateArticleTag(articleTag);
        return ResponseEntity.ok(articleTagService.getTagById(articleTag.getId()));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete tag", tags = { "Article Tag" })
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "admin role required")})
    @DeleteMapping("/{tag_id}")
    ResponseEntity deleteUser(@PathVariable long tag_id) {
        if (!securityUtilsService.isAuthorityReachableForLoggedUser(new Role("ROLE_ADMIN")))
            return ResponseEntity.noContent().build();

        ArticleTag articleTag = articleTagService.getTagById(tag_id);
        articleTagService.deleteArticleTag(articleTag);
        return ResponseEntity.noContent().build();
    }
}
