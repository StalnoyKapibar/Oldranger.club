package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "Scrollable topics")
public class ScrollableTopicsRestController {

    private TopicService topicService;
    private SubsectionService subsectionService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get TopicAndNewMessagesCountDto list", tags = { "Scrollable topics" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TopicAndNewMessagesCountDto or empty array if section doesn't contain topics",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicAndNewMessagesCountDto.class)))) })
    @GetMapping(value = "/subsection/{subsectionId}", produces = { "application/json" })
    public ResponseEntity<List<TopicAndNewMessagesCountDto>> getPart(@PathVariable long subsectionId,
                                                                     @RequestParam(value = "page", required = false) Integer page) {

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 20, Sort.by("topic_id"));

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
