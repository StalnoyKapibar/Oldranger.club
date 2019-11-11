package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.ForumTreeAdminService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminRestController {

    private ForumTreeAdminService forumTreeAdminService;

    @GetMapping("/allsectionsandsubsections")
    public ResponseEntity<List<Section>> getSectionsAndTopicsDto() {
        List<Section> dtos = forumTreeAdminService.getAllSections();
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/swapsections")
    public ResponseEntity swapSections(@RequestBody List<Long> sectionsId) {
        forumTreeAdminService.swapSectons(sectionsId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/swapsubsections")
    public ResponseEntity swapSubsections(@RequestBody Map<Long, List<String>> sectionsAndSubsectionsIds) {
        // клиент посылает это: {"1":["subId-3.3","subId-1.1","subId-1.2","subId-1.3"],"3":["subId-3.1","subId-3.2"]}
        forumTreeAdminService.swapSubsectons(sectionsAndSubsectionsIds);
        return ResponseEntity.ok().build();
    }
}
