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
                              @PageableDefault(size = 5, sort = "user_id") Pageable pageable) {

        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("user_id"));
        }

        Page<UserStatistic> users = userStatisticService.getAllUserStatistic(pageable);
        model.addAttribute("users", users);
        model.addAttribute("pageCount", users.getTotalPages());
        model.addAttribute("usersList", users.getContent());
        return "users";
    }
}
