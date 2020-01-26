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
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/articleTagsNode")
@Tag(name = "Article Tags Node")
public class ArticleTagsNodeRestController {

    private ArticleTagsNodeService articleTagsNodeService;
    private SecurityUtilsService securityUtilsService;
    private ArticleTagService articleTagService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all article tags nodes", tags = {"Article Tags Node"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTagsNode.class)))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @GetMapping(value = "", produces = {"application/json"})
    public ResponseEntity<List<ArticleTagsNode>> getAllTagsNodes() {
        if (!securityUtilsService.isAdmin())
            return ResponseEntity.noContent().build();

        List<ArticleTagsNode> tagsNodes = articleTagsNodeService.getAllTagsNodes();
        return ResponseEntity.ok(tagsNodes);
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Create new tags node", tags = {"Article Tags Node"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTagsNode.class))),
            @ApiResponse(responseCode = "204", description = "admin role required")})
    @PostMapping(value = "")
    public ResponseEntity<ArticleTagsNode> createTag(@RequestParam("parentId") long parentId,
                                                @RequestParam("tagId") long tagId, @RequestParam("position") int position) {
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        ArticleTagsNode articleTagsNode = new ArticleTagsNode(articleTagsNodeService.getTagsNodeById(parentId),
                position, articleTagService.getTagById(tagId));

        articleTagsNodeService.addTagsNode(articleTagsNode);
        return ResponseEntity.ok(articleTagsNode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleTagsNode> getTagsNodeById(@PathVariable("id") long id) {
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articleTagsNodeService.getTagsNodeById(id));
    }

    //    @Operation(security = @SecurityRequirement(name = "security"),
//            summary = "Get all article tags nodes tree", tags = {"Article Tags Node Tree"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200",
//                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTagsNode.class)))),
//            @ApiResponse(responseCode = "204", description = "admin role required")})
    @GetMapping(value = "/tree", produces = {"application/json"})
    public ResponseEntity<List<ArticleTagsNode>> getAllTagsNodesTree() {
        if (!securityUtilsService.isAdmin())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(articleTagsNodeService.getAllTagsNodesTree());
    }


}
