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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.utils.SearchService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
            @ApiResponse(responseCode = "404", description = "Topics not found")})
    @GetMapping(value = "/searchTopics", produces = {"application/json"})
    public ResponseEntity<?> getFindTopics(@Parameter(description = "Ключевое слово поиска")
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
        if (topics == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Нет результатов по запросу");
        }
        if (currentUser == null) {
            topics = topics.stream().filter(x -> !x.isHideToAnon()).collect(Collectors.toList());
            try {
                SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(topics.get(0).getSection(), topics);
                return ResponseEntity.ok(sectionsAndTopicsDto);
            } catch (IndexOutOfBoundsException e) {
                log.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } else {
            try {
                SectionsAndTopicsDto sectionsAndTopicsDto = new SectionsAndTopicsDto(topics.get(0).getSection(), topics);
                return ResponseEntity.ok(sectionsAndTopicsDto);
            } catch (IndexOutOfBoundsException e) {
                log.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
    public ResponseEntity<?> getFindComments(@Parameter(description = "Ключевое слово поиска")
                                             @RequestParam(value = "finderTag") String finderTag,
                                             @RequestParam(value = "page", required = false) Integer page,
                                             @RequestParam(value = "limit", required = false) Integer limit) {
        User currentUser = securityUtilsService.getLoggedUser();
        List<Comment> comments = searchService.searchByComment(finderTag, page, limit);
        if (comments == null || comments.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Нет результатов по запросу");
        }
        List<CommentDto> commentDtoList = comments
                .stream()
                .map(a -> commentService.assembleCommentDto(a, currentUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentDtoList);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get found article by ArticleTitle", tags = {"Search Articles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "204", description = "Articles not found"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @GetMapping(value = "/searchArticles", produces = {"application/json"})
    public ResponseEntity<?> getAllArticlesByArticleTitle(@RequestParam(value = "title") String title,
                                                          @RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "limit", required = false) Integer limit) {
        User user = securityUtilsService.getLoggedUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (title == null) {
            return ResponseEntity.noContent().build();
        }
        List<Article> allArticle = searchService.searchByArticleName(title);
        List<Article> articlesList = searchService.searchByArticleNameLimitPage(title, page, limit);

        return ResponseEntity.ok(articleService.assembleArticleListAndCountArticleDto(articlesList, allArticle.size()));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get articles by tags", description = "Get articles by tags", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Article.class)))),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @GetMapping(value = "/tag", produces = {"application/json"})
    public ResponseEntity<Page<Article>> getAllArticlesByTagId(@RequestParam Set<ArticleTag> tag_id,
                                                               @RequestParam(value = "page", required = false) Integer page) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (page == null || tag_id.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
            Page<Article> articles = articleService.getAllByTag(tag_id, pageable);
            return ResponseEntity.ok(articles);
        }
    }
}