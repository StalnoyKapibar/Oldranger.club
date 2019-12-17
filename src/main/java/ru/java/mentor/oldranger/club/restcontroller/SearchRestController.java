package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SearchRestController {
    SearchService searchService;
    CommentService commentService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get found topics", tags = {"Search API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = SectionsAndTopicsDto.class))),
            @ApiResponse(responseCode = "204", description = "Topics not found")})
    @GetMapping(value = "/searchTopics", produces = {"application/json"})
    public ResponseEntity<SectionsAndTopicsDto> getFindTopics(@Parameter(description = "Ключевое слово поиска",
            schema = @Schema(description = "Топик"))
                                                              @RequestParam(value = "finderTag") String finderTag,
                                                              @Parameter(description = "0 - везде, 1 - в разделе, 2 - в подразделе.")
                                                              @RequestParam(value = "node") Integer node,
                                                              @Parameter(description = "Значение узла(ид - раздела, подраздела).")
                                                              @RequestParam(value = "nodeValue") Long nodeValue) {
        List<Topic> topics = searchService.searchTopicsByNode(finderTag, node, nodeValue);

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
                    content = @Content(schema = @Schema(implementation = SectionsAndTopicsDto.class))),
            @ApiResponse(responseCode = "204", description = "Comments not found")})
    @GetMapping(value = "/searchComments", produces = {"application/json"})
    public ResponseEntity<List<CommentDto>> getFindComments(@Parameter(description = "Ключевое слово поиска")
                                                            @RequestParam(value = "finderTag") String finderTag) {
        List<Comment> comments = searchService.searchByComment(finderTag);
        if (comments.size() > 0) {
            List<CommentDto> commentDtoList = comments.stream().map(commentService::assembleCommentDto).collect(Collectors.toList());
            return ResponseEntity.ok(commentDtoList);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
