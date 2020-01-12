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
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("api/article")
@Tag(name = "ArticleLike")
public class ArticleLikeUsersRestController {


    private ArticleService articleService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Set likes users or dislikes users", tags = { "Admin" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Article.class)))) })
    @PostMapping(value = "/{articles_id}/like")
    public ResponseEntity<Set<User>> getLikeUsersByArticlesId(@PathVariable long articles_id) {
        Article article = articleService.getArticleById(articles_id);
        User user = securityUtilsService.getLoggedUser();
        Set<User> likes = article.getLikes();
        if (likes.contains(user)) {
            likes.remove(user);
            article.setLikes(likes);
            articleService.addArticle(article);
        } else {
            likes.add(user);
            article.setLikes(likes);
            articleService.addArticle(article);
        }
        return ResponseEntity.ok(article.getLikes());
    }

}
