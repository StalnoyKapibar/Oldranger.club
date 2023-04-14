package ru.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import ru.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.oldranger.club.model.forum.Subsection;
import ru.oldranger.club.model.forum.Topic;
import ru.oldranger.club.service.forum.SubsectionService;
import ru.oldranger.club.service.forum.TopicService;

import java.util.ArrayList;
import java.util.Collections;
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
                                                                     @Parameter(description="Topic list is pageable, so you can set page number;" +
                                                                             " default page size is 20 (currently hardcoded); not required")
                                                                     @RequestParam(value = "page", required = false) Integer page,
                                                                     @Parameter(description="yyyy-MM-dd HH:mm:ss (for example 2019-10-31 18:33:36)",
                                                                             required=true)
                                                                     @RequestParam(value = "dateTime") String dateTime,
                                                                     @Parameter(description="По умолчанию топики выдаются в порядке убывания" +
                                                                             " по времени последнего ответа - от более новых к более старым; " +
                                                                             "для обратного порядка передать параметр reversed=1")
                                                                         @RequestParam(value = "reversed", required = false) Integer reversed) {

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id"));

        Optional<Subsection> optionalSubsection = subsectionService.getById(subsectionId);

        if (!optionalSubsection.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        Subsection subsection = optionalSubsection.get();

        Page<Topic> pageableTopicsBySubsection = topicService.getPageableBySubsectionWithFixTime(subsection, dateTime, pageable);

        if (pageableTopicsBySubsection.getNumberOfElements() == 0) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<TopicAndNewMessagesCountDto> dtos = topicService.getTopicsDto(pageableTopicsBySubsection.getContent());

        if (reversed != null && dtos != null & reversed == 1) {
            Collections.reverse(dtos);
        }

        return ResponseEntity.ok(dtos);
    }
}
