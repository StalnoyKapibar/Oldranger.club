package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity swapSections(@RequestBody List<Long> sectionsId) {
        sectionsAndSubsectionsService.swapSections(sectionsId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/swapsubsections")
    public ResponseEntity swapSubsections(@RequestBody Map<Long, List<String>> sectionsAndSubsectionsIds) {
        sectionsAndSubsectionsService.swapSubsectons(sectionsAndSubsectionsIds);
        return ResponseEntity.ok().build();
    }

}
