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
    public ResponseEntity updateUser(@RequestBody Map<String, Long> swapSections) {
        forumTreeAdminService.swapSectons(swapSections);
        return ResponseEntity.ok().build();
    }

}
