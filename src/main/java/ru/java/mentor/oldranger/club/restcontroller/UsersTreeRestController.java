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
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UsersTreeService;

import java.util.List;
import java.util.TreeMap;

@RestController
@AllArgsConstructor
@RequestMapping("/api/usersTree")
public class UsersTreeRestController {

    private UsersTreeService usersTreeService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get invited users tree by id user inviting", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")})
    @GetMapping(value = "/user/{nameUser}")
    public ResponseEntity<TreeMap<String, List<User>>> getInvitedUsersTreeById(@PathVariable String nameUser) {
        //List<User> userList = usersTreeService.getInvitedUsersTreeById(nameUser, 0);
        TreeMap<String, List<User>> treeMap = usersTreeService.getInvitedUsersTreeById(nameUser, 0);
        return ResponseEntity.ok(treeMap);
    }
    }
