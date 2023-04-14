package ru.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.article.ArticleLikeService;
import ru.oldranger.club.service.utils.SecurityUtilsService;

@RestController
@AllArgsConstructor
@RequestMapping("api/article")
@Tag(name = "ArticleLike")
public class ArticleLikeUsersRestController {


    private ArticleLikeService articleLikeService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Set likes users or dislikes users", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")})
    @PostMapping(value = "/{articleId}/like")
    public void makeLike(@PathVariable long articleId) {
        User user = securityUtilsService.getLoggedUser();
        if (!articleLikeService.isUserLiked(articleId, user.getId()).isEmpty()) {
            articleLikeService.deleteLike(articleId, user.getId());
        } else {
            articleLikeService.saveLike(articleId, user.getId());
        }

    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get likes users article", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")})
    @GetMapping(value = "/{articleId}/likes")
    public long getLikeUsersByArticlesId(@PathVariable long articleId) {
        return articleLikeService.countLikes(articleId);
    }

}
