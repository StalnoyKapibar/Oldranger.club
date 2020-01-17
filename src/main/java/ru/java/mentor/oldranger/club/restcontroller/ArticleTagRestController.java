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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
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
            summary = "Get all article tags", tags = {"Article Tag"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTag.class)))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @GetMapping(value = "", produces = {"application/json"})
    public ResponseEntity<List<ArticleTag>> getTag() {
        if (!securityUtilsService.isAdmin())
            return ResponseEntity.noContent().build();

        List<ArticleTag> tags = articleTagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Create new tag", tags = {"Article Tag"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTag.class))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @PostMapping(value = "")
    public ResponseEntity<ArticleTag> createTag(@RequestParam("id") long id,
                                                @RequestParam("name") String name) {
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        ArticleTag articleTag = new ArticleTag(id, name);
        articleTagService.addTag(articleTag);
        return ResponseEntity.ok(articleTag);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Edit tag", tags = {"Article Tag"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTag.class))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @PutMapping(value = "/{tag_id}")
    public ResponseEntity<ArticleTag> updateTag(@PathVariable long tag_id,
                                                @RequestParam String tagName) {
        if (!securityUtilsService.isAdmin())
            return ResponseEntity.noContent().build();
        ArticleTag articleTag = new ArticleTag(tag_id, tagName);
        articleTagService.updateArticleTag(articleTag);
        return ResponseEntity.ok(articleTagService.getTagById(articleTag.getId()));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete tag", tags = {"Article Tag"})
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "admin role required")})
    @DeleteMapping("/{tag_id}")
    ResponseEntity deleteUser(@PathVariable("tag_id") long id) {
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }

        ArticleTag articleTag = articleTagService.getTagById(id);
        articleTagService.deleteArticleTag(articleTag);
        return ResponseEntity.noContent().build();
    }
}
