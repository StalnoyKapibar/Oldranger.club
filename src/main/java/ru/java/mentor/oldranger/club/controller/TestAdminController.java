package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

@Deprecated
@Hidden
@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class TestAdminController {

    UserStatisticService userStatisticService;
    MessageService messageService;
    EmailDraftService emailDraftService;
    MailService mailService;
    UserService userService;
    // ToDo DELETE ПОДУМАТЬ удалить ли этот метод так как есть rest аналог  http://localhost:8888/api/admin/users  &   http://localhost:8888/users   ВМЕСТО текущего http://localhost:8888/admin/users
    @GetMapping("/users")
    public String getAllUsers(Model model,
                              @RequestAttribute(value = "page", required = false) Integer page,
                              @PageableDefault(size = 5, sort = "user_id") Pageable pageable,
                              @RequestParam(value = "query", required = false) String query) {

        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("user_id"));
        }

        Page<UserStatisticDto> users = userStatisticService.getAllUserStatistic(pageable);

        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim().replaceAll("\\s++", ",");
            users = userStatisticService.getUserStatisticsByQuery(pageable, query);
        } else {
            query = null;
        }

        model.addAttribute("users", users);
        model.addAttribute("pageCount", users.getTotalPages());
        model.addAttribute("usersList", users.getContent());
        model.addAttribute("query", query);
        return "admin/users";
    }


    // в админке получить список сеций и подсекций с возможностью сортировки
    @GetMapping("/sectionsandsubsections")
    //ToDo DELETE ПОДУМАТЬ удалить ли этот метод так как есть аналог в REST http://localhost:8888/api/allsectionsandsubsections вместо текущего    http://localhost:8888/admin/sectionsandsubsections
    public String getPageSections() {
        return "testSortableSectionsAndSubsections";
    }


    //ToDo DELETE ПОДУМАТЬ удалить ли этот метод так как есть аналог в REST http://localhost:8888/api/chat/users вместо текущего:  http://localhost:8888/admin/chat
    @GetMapping("/chat")
    public String getChatSettings() {
        return "admin/chatSettings";
    }

    //ToDo DELETE ПОДУМАТЬ удалить ли этот метод НО  в REST точного аналога не нашел хотя похожие есть http://localhost:8888/api/chat/image ( место текущего:  http://localhost:8888/admin/chat?cleanChat=1  )
    @PostMapping("/chat")
    public String setChatSettings(@RequestParam String cleanChat) {
        messageService.setOlderThan(cleanChat);
        return "redirect:/admin/chatSettings";
    }

    //ToDo DELETE ПОДУМАТЬ удалить ли этот метод НО  в REST точного аналога не нашел
    @GetMapping("/mail")
    public String getMailPage(){
        return "admin/mail";
    }
}
