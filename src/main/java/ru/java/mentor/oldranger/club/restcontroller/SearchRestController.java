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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Tag(name = "Search API")
@RequestMapping("/api")
public class SearchRestController {
    private SearchService searchService;
    private CommentService commentService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get found topics", tags = {"Search API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = SectionsAndTopicsDto.class))),
            @ApiResponse(responseCode = "204", description = "Topics not found")})
    @GetMapping(value = "/searchTopics", produces = {"application/json"})
    public ResponseEntity<SectionsAndTopicsDto> getFindTopics(@Parameter(description = "Ключевое слово поиска")
                                                              @RequestParam(value = "finderTag") String finderTag,
                                                              @Parameter(description = "page")
                                                              @RequestParam(value = "page", required = false) Integer page,
                                                              @Parameter(description = "limit")
                                                              @RequestParam(value = "limit", required = false) Integer limit,
                                                              @Parameter(description = "0 - везде, 1 - в разделе, 2 - в подразделе.")
                                                              @RequestParam(value = "node", required = false) Integer node,
                                                              @Parameter(description = "Значение узла(ид - раздела, подраздела).")
                                                              @RequestParam(value = "nodeValue", required = false) Long nodeValue) {
        List<Topic> topics = searchService.searchTopicsByPageAndLimits(finderTag, page, limit, node, nodeValue);

        try {
            SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(topics.get(0).getSection(), topics);
            return ResponseEntity.ok(sectionsAndTopicsDto);
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get found comments by finderTag", tags = {"Search API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))),
            @ApiResponse(responseCode = "204", description = "Comments not found")})
    @GetMapping(value = "/searchComments", produces = {"application/json"})
    public ResponseEntity<List<CommentDto>> getFindComments(@Parameter(description = "Ключевое слово поиска")
                                                            @RequestParam(value = "finderTag") String finderTag,
                                                            @RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "limit", required = false) Integer limit) {
        List<Comment> comments = searchService.searchByComment(finderTag, page, limit);
        if (comments == null) {
            return ResponseEntity.noContent().build();
        }
        List<CommentDto> commentDtoList = comments.stream().map(commentService::assembleCommentDto).collect(Collectors.toList());

        return ResponseEntity.ok(commentDtoList);
    }
}
