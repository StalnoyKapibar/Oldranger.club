package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ScrollableTopicsRestController {

    private TopicService topicService;

    private SubsectionService subsectionService;

    @GetMapping("/subsection/{subsectionId}")
    public ResponseEntity<List<TopicAndNewMessagesCountDto>> getPart(@PathVariable long subsectionId,
                                        @PageableDefault(size = 20) Pageable pageable) {

        Optional<Subsection> optionalSubsection = subsectionService.getById(subsectionId);

        if (!optionalSubsection.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        Subsection subsection = optionalSubsection.get();

        Page<Topic> pageableTopicsBySubsection = topicService.getPageableBySubsection(subsection, pageable);

        if (pageableTopicsBySubsection.getNumberOfElements() == 0) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        int currentPageNumber = pageable.getPageNumber();
        String nextPageLink = null;

        if (pageableTopicsBySubsection.getTotalPages() > currentPageNumber && !pageableTopicsBySubsection.isLast()) {
            nextPageLink = "?page=" + (currentPageNumber + 1);
        }

        List<TopicAndNewMessagesCountDto> dtos = topicService.getTopicsDto(pageableTopicsBySubsection.getContent());
        return ResponseEntity.ok(dtos);
    }
}
