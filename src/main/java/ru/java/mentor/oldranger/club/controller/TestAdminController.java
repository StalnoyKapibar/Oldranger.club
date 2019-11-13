package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class TestAdminController {

    UserStatisticService userStatisticService;

    @GetMapping("/users")
    public String getAllUsers(Model model,
                              @RequestAttribute(value = "page", required = false) Integer page,
                              @PageableDefault(size = 5, sort = "user_id") Pageable pageable,
                              @RequestParam(value = "query", required = false) String query) {

        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("user_id"));
        }


        Page<UserStatistic> users = userStatisticService.getAllUserStatistic(pageable);

        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim().replaceAll("\\s++", ",");
            users = userStatisticService.getUserStatisticsByQuery(pageable, query);
        } else { query = null; }

        model.addAttribute("users", users);
        model.addAttribute("pageCount", users.getTotalPages());
        model.addAttribute("usersList", users.getContent());
        model.addAttribute("query", query);
        return "users";
    }

    // в админке получить список сеций и подсекций с возможностью сортировки
    @GetMapping("/sectionsandsubsections")
    public String getPageSections() {
        return "testSortableSectionsAndSubsections";
    }

}
