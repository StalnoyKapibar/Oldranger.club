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
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tags/node")
@Tag(name = "Article TagsNode")
public class ArticleTagsNodeRestController {

    private ArticleTagsNodeService tagsNodeService;
    private SecurityUtilsService securityUtilsService;
    private ArticleTagService articleTagService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all nodes of tags", description = "Get full tree with child", tags = {"Article TagsNode"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTagsNode.class)))),
            @ApiResponse(responseCode = "204", description = "Nodes not found")})
    @GetMapping(value = "/get", produces = {"application/json"})
    public ResponseEntity<List<ArticleTagsNode>> getFullTree() {
        List<ArticleTagsNode> tagsNodes = tagsNodeService.findAll();
        if (tagsNodes.isEmpty() || !securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tagsNodes);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get tree of tags dto (menu tree)", description = "Get full dto tree with child", tags = {"Article TagsNode"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTagsNodeDto.class)))),
            @ApiResponse(responseCode = "204", description = "Nodes not found")})
    @GetMapping(value = "/tree", produces = {"application/json"})
    public ResponseEntity<List<ArticleTagsNodeDto>> getAllTagsNodesTree() {
        List<ArticleTagsNodeDto> tagsNodeDto = tagsNodeService.findHierarchyTreeOfAllTagsNodes();
        if (securityUtilsService.getLoggedUser() == null || tagsNodeDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tagsNodeDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get node by id", description = "Get node by id", tags = {"Article TagsNode"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleTagsNode.class)))),
            @ApiResponse(responseCode = "204", description = "Node not found")})
    @GetMapping(value = "/{id}", produces = {"application/json"})
    public ResponseEntity<ArticleTagsNode> getNodeById(@PathVariable("id") Long id) {
        ArticleTagsNode tagsNode = tagsNodeService.findById(id);
        if (id == null || tagsNode == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tagsNode);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Create new node by parentId, position, tagName(if tag == null, then create new tag)", tags = {"Article TagsNode"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTagsNode.class))),
            @ApiResponse(responseCode = "400", description = "Admin role required")})
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResponseEntity<ArticleTagsNode> createNode(@RequestParam("parentId") Long parentId,
                                                      @RequestParam("position") Integer position,
                                                      @RequestParam("tagName") String tagName) {
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.badRequest().build();
        }
        ArticleTag tag = articleTagService.getTagByTagName(tagName);
        if (tag == null) {
            tag = new ArticleTag();
            tag.setName(tagName);
            articleTagService.addTag(tag);
        }
        ArticleTagsNode tagsNode = new ArticleTagsNode(tagsNodeService.findById(parentId), position, tag);
        tagsNodeService.save(tagsNode);
        return ResponseEntity.ok(tagsNode);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Edit node by id, change params parentId, tagName, position, if tag == null, then create new tag", tags = {"Article TagsNode"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleTagsNode.class))),
            @ApiResponse(responseCode = "400", description = "Error editing node")})
    @PutMapping(value = "/update", produces = {"application/json"})
    public ResponseEntity<ArticleTagsNode> updateNode(@RequestParam("id") Long id,
                                                      @RequestParam("parentId") Long parentId,
                                                      @RequestParam("tagName") String tagName,
                                                      @RequestParam("position") Integer pos) {

        ArticleTagsNode tagsNodeForUpdate = tagsNodeService.findById(id);
        if (tagsNodeForUpdate == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.badRequest().build();
        }
        if (id == parentId) {
            return ResponseEntity.badRequest().build();
        }
        ArticleTagsNode parentTagsNode = tagsNodeService.findById(parentId);
        tagsNodeForUpdate.setParent(parentTagsNode);
        tagsNodeForUpdate.setPosition(pos);
        ArticleTag tag = articleTagService.getTagByTagName(tagName);
        if (tag == null) {
            tag = new ArticleTag();
            tag.setName(tagName);
            articleTagService.addTag(tag);
        }
        tagsNodeForUpdate.setTag(tag);
        tagsNodeService.save(tagsNodeForUpdate);
        return ResponseEntity.ok(tagsNodeForUpdate);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete node", description = "Delete node with all child", tags = {"Article TagsNode"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Error deleting node")})
    @DeleteMapping(value = "/delete")
    public ResponseEntity deleteNode(@RequestParam("id") Long id) {

        if (id == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.badRequest().build();
        }
        tagsNodeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
