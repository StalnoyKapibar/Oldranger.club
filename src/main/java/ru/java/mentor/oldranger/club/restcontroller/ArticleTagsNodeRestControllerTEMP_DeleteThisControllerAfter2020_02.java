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
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/temp/articleTagsNode")
@Tag(name = "Article Tags Node")
public class ArticleTagsNodeRestControllerTEMP_DeleteThisControllerAfter2020_02 {

    private ArticleTagsNodeService articleTagsNodeService;
    private SecurityUtilsService securityUtilsService;
    private ArticleTagService articleTagService;

    @GetMapping(value = "/tree", produces = {"application/json"})
    public ResponseEntity<List<ArticleTagsNodeDto>> getAllTagsNodesTree() {
        if (!securityUtilsService.isAdmin())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(articleTagsNodeService.findHierarchyTreeOfAllTagsNodes());
    }

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

        return ResponseEntity.ok(articleTagsNodeService.findAll());
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
        ArticleTagsNode articleTagsNode = new ArticleTagsNode(articleTagsNodeService.findById(parentId),
                position, articleTagService.getTagById(tagId));

        articleTagsNodeService.save(articleTagsNode);
        return ResponseEntity.ok(articleTagsNode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleTagsNode> getTagsNodeById(@PathVariable("id") long id) {
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articleTagsNodeService.findById(id));
    }

    @PostMapping(value = "/update")
    public ResponseEntity udateNode(@RequestParam("id") Long id,
                                    @RequestParam("parentId") Long parent_id,
                                    @RequestParam("tagId") Long tagId,
                                    @RequestParam("position") Integer position) {

        if (id == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.badRequest().build();
        }
        ArticleTagsNode node = articleTagsNodeService.findById(id);
        node.setPosition(position);
        node.setParent(articleTagsNodeService.findById(parent_id));
        node.setTag(articleTagService.getTagById(tagId));
        articleTagsNodeService.save(node);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteNode(@PathVariable(value = "id") Long id) {
        if (id == null || id < 0 || !securityUtilsService.isAdmin()) {
            return ResponseEntity.badRequest().build();
        }
        articleTagsNodeService.deleteById(id);
        return ResponseEntity.ok().build();
    }


}
