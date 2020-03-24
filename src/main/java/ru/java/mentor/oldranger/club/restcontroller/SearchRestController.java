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
import ru.java.mentor.oldranger.club.dto.*;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.utils.SearchService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Tag(name = "Search API")
@RequestMapping("/api")
public class SearchRestController {
    private SearchService searchService;
    private CommentService commentService;
    private SecurityUtilsService securityUtilsService;
    private ArticleService articleService;

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
        User currentUser = securityUtilsService.getLoggedUser();
        List<Topic> topics = searchService.searchTopicsByPageAndLimits(finderTag, page, limit, node, nodeValue);
        if(currentUser == null) {
            topics = topics.stream().filter(x -> !x.isHideToAnon()).collect(Collectors.toList());
            try {
                SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(topics.get(0).getSection(), topics);
                return ResponseEntity.ok(sectionsAndTopicsDto);
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.noContent().build();
            }
        } else {
            try {
                SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(topics.get(0).getSection(), topics);
                return ResponseEntity.ok(sectionsAndTopicsDto);
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.noContent().build();
            }
        }
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get found comments by finderTag", tags = {"Search API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))),
            @ApiResponse(responseCode = "204", description = "Comments not found")})
    @GetMapping(value = "/searchComments", produces = {"application/json"})
    public ResponseEntity<List<CommentDto>> getFindComments(@SessionAttribute User currentUser,
                                                            @Parameter(description = "Ключевое слово поиска")
                                                            @RequestParam(value = "finderTag") String finderTag,
                                                            @RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "limit", required = false) Integer limit) {
        List<Comment> comments = searchService.searchByComment(finderTag, page, limit);
        if (comments == null) {
            return ResponseEntity.noContent().build();
        }
        List<CommentDto> commentDtoList = comments.stream().map(a->commentService.assembleCommentDto(a, currentUser)).collect(Collectors.toList());

        return ResponseEntity.ok(commentDtoList);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get found article", tags = {"Search API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = SectionsAndTopicsDto.class))),
            @ApiResponse(responseCode = "204", description = "Articles not found")})
    @GetMapping(value = "/searchArticles", produces = {"application/json"})
    public ResponseEntity<ArticleAndCommentsDto> getFindArticles(@Parameter(description = "Ключевое слово поиска")
                                                              @RequestParam(value = "finderTag") String finderTag,
                                                              @Parameter(description = "page")
                                                              @RequestParam(value = "page", required = false) Integer page,
                                                              @Parameter(description = "limit")
                                                              @RequestParam(value = "limit", required = false) Integer limit,
                                                              @Parameter(description = "0 - везде, 1 - в разделе, 2 - в подразделе.")
                                                              @RequestParam(value = "node", required = false) Integer node,
                                                              @Parameter(description = "Значение узла(ид - раздела, подраздела).")
                                                              @RequestParam(value = "nodeValue", required = false) Long nodeValue) {
        User currentUser = securityUtilsService.getLoggedUser();
        List<Article> articles = searchService.searchTopicsByPageAndLimits(finderTag, page, limit, node, nodeValue);
        List<ArticleCommentDto> articleCommentsDto;
        ArticleAndCommentsDto articleAndCommentsDto;

        if (articles == null) {
            return ResponseEntity.noContent().build();
        }
        for (Article article: articles){
            articleCommentsDto = articleService.getAllByArticle(article);
            articleAndCommentsDto = articleService.getArticleAndArticleCommentDto(article, articleCommentsDto);
        }
       articles.stream().map(a->articleService.getArticleAndArticleCommentDto().collect(Collectors.toList());

        if(currentUser == null) {
            articles = articles.stream().filter(x -> !x.isHideToAnon()).collect(Collectors.toList());
            try {
                articles.get(0).getArticleTags();
                ArticleTagsNodeDto articleTagsNodeDto = new ArticleTagsNodeDto(articles.get(0), articles);
              //  SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(articles.get(0).getSection(), topics);
                return ResponseEntity.ok(sectionsAndTopicsDto);
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.noContent().build();
            }
        } else {
            try {
                SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(topics.get(0).getSection(), topics);
                return ResponseEntity.ok(sectionsAndTopicsDto);
            } catch (IndexOutOfBoundsException e) {
                return ResponseEntity.noContent().build();
            }
        }
    }
}
