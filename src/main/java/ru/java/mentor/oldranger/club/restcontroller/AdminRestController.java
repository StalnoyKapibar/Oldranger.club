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
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminRestController {

    UserStatisticService userStatisticService;
    MessageService messageService;
    EmailDraftService emailDraftService;
    MailService mailService;
    UserService userService;

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



    @GetMapping("/getDrafts")
    public ResponseEntity<List<EmailDraft>> getDrafts(@RequestParam(value = "page", required = false) Integer page,
                              @PageableDefault(size = 5, sort = "lastEditDate", direction = Sort.Direction.DESC) Pageable pageable) {
        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("lastEditDate").descending());
        }
        List<EmailDraft> drafts = emailDraftService.getAllDraftsPageable(pageable).getContent();
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/getTotalPages")
    public ResponseEntity<Map<String,Integer>> getDrafts(@PageableDefault(size = 5) Pageable pageable) {
        Integer pages = emailDraftService.getAllDraftsPageable(pageable).getTotalPages();
        Map<String,Integer> totalPages = new HashMap<>();
        totalPages.put("totalPages", pages);
        return ResponseEntity.ok(totalPages);
    }

    @PostMapping("/sendMail")
    public ResponseEntity<String> sendMail(EmailDraft draft) {
//        List<User> users = userService.findAll();
//        List<String> mailList = new ArrayList<>();
//        users.forEach(user -> mailList.add(user.getEmail()));
//        String[] emails = (String[]) mailList.toArray();
        String[] emails = new String[]{"swish92@gmail.com"};
        try {
            mailService.sendHtmlMessage(emails, draft);
        } catch (MessagingException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getDraft/{id}")
    public ResponseEntity<EmailDraft> editDraft(@PathVariable long id) {
        EmailDraft draft = emailDraftService.getById(id);
        return ResponseEntity.ok(draft);
    }

    @GetMapping("/deleteDraft/{id}")
    public ResponseEntity<String> deleteDraft(@PathVariable long id) {
        try {
            emailDraftService.deleteDraft(id);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saveDraft")
    public ResponseEntity<String> saveDraft(EmailDraft draft) {
        draft.setLastEditDate(LocalDateTime.now());
        try {
            emailDraftService.saveDraft(draft);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

}
