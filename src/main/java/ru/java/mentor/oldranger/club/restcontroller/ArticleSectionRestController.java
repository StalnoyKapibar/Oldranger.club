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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.ArticleSectionDto;
import ru.java.mentor.oldranger.club.dto.TopicAndCommentsDTO;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;
import ru.java.mentor.oldranger.club.service.article.ArticleSectionService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/articleSection")
@Tag(name = "Article Section")
public class ArticleSectionRestController {

    private ArticleSectionService articleSectionService;
    private SecurityUtilsService securityUtilsService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a section with articles", description = "Get a section with pageable articles by section id", tags = {"Article Section"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleSectionDto.class)))),
            @ApiResponse(responseCode = "204", description = "Invalid section id")})
    @GetMapping(value = "/articles", produces = {"application/json"})
    public ResponseEntity<ArticleSectionDto> getSectionAndArticles(@RequestParam("sectionId") Long id,
                                                                   @RequestParam(value = "page", required = false) Integer page) {
        if (id == null) {
            return ResponseEntity.noContent().build();
        }
        ArticlesSection articlesSection = articleSectionService.getSectionById(id);

        if (page == null) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        Page<Article> articlePage = articleSectionService.getPageableArticlesBySection(articlesSection, pageable);
        ArticleSectionDto articleSectionDto = new ArticleSectionDto(articlesSection, articlePage);
        return ResponseEntity.ok(articleSectionDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all sections", tags = {"Article Section"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticlesSection.class))))})
    @GetMapping(value = "/all", produces = {"application/json"})
    public ResponseEntity<List<ArticlesSection>> getAllSections() {
        List<ArticlesSection> articlesSections = articleSectionService.getAllArticlesSection();
        return ResponseEntity.ok(articlesSections);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get section by id without articles", tags = {"Article Section"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticlesSection.class))),
            @ApiResponse(responseCode = "204", description = "Invalid section id")})
    @GetMapping(value = "/{id}", produces = {"application/json"})
    public ResponseEntity<ArticlesSection> getSectionById(@PathVariable(value = "id") Long id) {

        if (id == null) {
            return ResponseEntity.noContent().build();
        }
        ArticlesSection articlesSection = articleSectionService.getSectionById(id);
        return ResponseEntity.ok(articlesSection);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Create new section", tags = {"Article Section"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticlesSection.class))),
            @ApiResponse(responseCode = "400", description = "Section must have a name"),
            @ApiResponse(responseCode = "204", description = "Admin role required")})
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResponseEntity<ArticlesSection> createSection(@RequestParam("id") Long id,
                                                         @RequestParam("name") String name) {
        if (name == null) {
            return ResponseEntity.badRequest().build();
        }
        ArticlesSection articlesSection = new ArticlesSection(id, name);
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        articleSectionService.addSection(articlesSection);
        return ResponseEntity.ok(articlesSection);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Edit section by id", tags = {"Article Section"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticlesSection.class))),
            @ApiResponse(responseCode = "400", description = "Invalid section id"),
            @ApiResponse(responseCode = "204", description = "Admin  role required")})
    @PutMapping(value = "/edit", produces = {"application/json"})
    public ResponseEntity<ArticlesSection> editSection(@RequestParam("id") Long id,
                                                       @RequestParam("name") String name) {

        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        ArticlesSection articlesSection = articleSectionService.getSectionById(id);
        articlesSection.setName(name);
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        articleSectionService.editSection(articlesSection);
        return ResponseEntity.ok(articlesSection);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete section by id", tags = {"Article Section"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Section deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid section id"),
            @ApiResponse(responseCode = "204", description = "Admin role required")})
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteSection(@PathVariable("id") Long id) {

        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        articleSectionService.deleteSection(id);
        return ResponseEntity.ok().build();
    }

}
