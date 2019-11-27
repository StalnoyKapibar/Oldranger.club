package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminRestController {

    UserStatisticService userStatisticService;

    @GetMapping("/users")
    public ResponseEntity<List<UserStatisticDto>> getAllUsers(@RequestParam(value = "page", required = false) Integer page,
                                                           @PageableDefault(size = 5, sort = "user_id") Pageable pageable,
                                                           @RequestParam(value = "query", required = false) String query) {
        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("user_id"));
        }

        List<UserStatistic> users = userStatisticService.getAllUserStatistic(pageable).getContent();

        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim().replaceAll("\\s++", ",");
            users = userStatisticService.getUserStatisticsByQuery(pageable, query).getContent();
        }

        List<UserStatisticDto> dtos = userStatisticService.getUserStatisticDtoFromUserStatistic(users);
        return ResponseEntity.ok(dtos);
    }

    // в админке получить список сеций и подсекций с возможностью сортировки
    @GetMapping("/sectionsandsubsections")
    public String getPageSections() {
        return "testSortableSectionsAndSubsections";
    }
}
