package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class TestAdminController {

    UserStatisticService userStatisticService;
    MessageService messageService;
    EmailDraftService emailDraftService;
    MailService mailService;
    UserService userService;

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
    public String getPageSections() {
        return "testSortableSectionsAndSubsections";
    }

    @GetMapping("/chat")
    public String getChatSettings() {
        return "admin/chatSettings";
    }

    @PostMapping("/chat")
    public String setChatSettings(@RequestParam String cleanChat) {
        messageService.setOlderThan(cleanChat);
        return "redirect:/admin/chatSettings";
    }

    @GetMapping("/mail")
    public String getMailPage(Model model,
                              @RequestParam(value = "editDraft",required = false) EmailDraft draft,
                              @RequestAttribute(value = "page", required = false) Integer page,
                              @PageableDefault(size = 5, sort = "lastEditDate", direction = Sort.Direction.DESC) Pageable pageable) {

        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("lastEditDate").descending());
        }
        Page<EmailDraft> drafts = emailDraftService.getAllDraftsPageable(pageable);
        if (draft != null){
            model.addAttribute("draft", draft);
        } else {
            model.addAttribute("draft", new EmailDraft());
        }
        model.addAttribute("drafts", drafts);
        model.addAttribute("draftList", drafts.getContent());
        model.addAttribute("pageCount", drafts.getTotalPages());
        return "admin/mail";
    }

    @PostMapping("/sendMail")
    public String sendMail(EmailDraft draft) {
        List<User> users = userService.findAll();
        List<String> mailList = new ArrayList<>();
        users.forEach(user -> mailList.add(user.getEmail()));
        String[] emails = (String[]) mailList.toArray();
        try {
            mailService.sendHtmlMessage(emails, draft);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/mail";
    }

    @GetMapping("/editDraft/{id}")
    public String editDraft(@PathVariable long id, RedirectAttributes ra) {
        EmailDraft draft = emailDraftService.getById(id);
        ra.addAttribute("editDraft",draft);
        return "redirect:/admin/mail";
    }

    @GetMapping("/deleteDraft/{id}")
    public String deleteDraft(@PathVariable long id) {
        try {
            emailDraftService.deleteDraft(id);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/admin/mail";
    }

    @PostMapping("/saveDraft")
    public String saveDraft(EmailDraft draft) {
        draft.setLastEditDate(LocalDateTime.now());
        emailDraftService.saveDraft(draft);
        return "redirect:/admin/mail";
    }
}
