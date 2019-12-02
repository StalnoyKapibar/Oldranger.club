package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class MainPageRestController {
    private SectionsAndTopicsService sectionsAndTopicsService;

    @GetMapping("")
    public ResponseEntity<List<SectionsAndTopicsDto>> mainPage() {
        List<SectionsAndTopicsDto> dtos = sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection();
        return ResponseEntity.ok(dtos);
    }
}
