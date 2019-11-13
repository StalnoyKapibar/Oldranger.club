package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SectionsAndSubsectionsRestController {

    private SectionsAndSubsectionsService sectionsAndSubsectionsService;

    @GetMapping("/allsectionsandsubsections")
    public ResponseEntity<List<SectionsAndSubsectionsDto>> getSectionsAndSubsectionsDto() {
        List<SectionsAndSubsectionsDto> dtos = sectionsAndSubsectionsService.getAllSectionsAndSubsections();
        if (dtos == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/swapsections")
    public ResponseEntity swapSections() {


        if (dtos == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok();
    }
}
