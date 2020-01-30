package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.UsersTreeDto;
import ru.java.mentor.oldranger.club.service.user.UsersTreeService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/usersTree")
public class UsersTreeRestController {

    private UsersTreeService usersTreeService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get invited users tree by id user inviting", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")})
    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<UsersTreeDto>> getInvitedUsersTreeById(@PathVariable long userId) {
        //List<User> userList = usersTreeService.getInvitedUsersTreeById(nameUser, 0);
//        List<List<User>> treeMap = usersTreeService.getInvitedUsersTreeById(nameUser, 0);
//        return ResponseEntity.ok(treeMap);
        //Map<String, List<UsersTreeDto>> treeMap = usersTreeService.getInvitedUsersTreeById(nameUser, deepTree);
        return ResponseEntity.ok(usersTreeService.getInvitedUsersTreeById(userId));
    }
}
