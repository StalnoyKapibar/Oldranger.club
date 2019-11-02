package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class SectionsAndTopicsServiceImpl implements SectionsAndTopicsService {

    private RoleHierarchy roleHierarchy;

    private SectionService sectionService;

    private TopicService topicService;

    private List<SectionsAndTopicsDto> combineListOfSectionsAndTopics(List<Section> sections, List<Topic> topics) {
        List<SectionsAndTopicsDto> dtos = new ArrayList<>();
        for (Section section : sections) {
            List<Topic> topicList = topics.stream().filter(topic -> topic.getSection().equals(section)).collect(Collectors.toList());
            SectionsAndTopicsDto dto = new SectionsAndTopicsDto(section, topicList);
            dtos.add(dto);
        }
        return dtos;
    }


    public List<SectionsAndTopicsDto> getAllSectionsAndActualTopicsLimit10BySection() {
        List<SectionsAndTopicsDto> sectionsAndTopicsDtos;
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        if (reachableGrantedAuthorities.contains(new Role("ROLE_USER"))) {
            sectionsAndTopicsDtos = combineListOfSectionsAndTopics(sectionService.getAllSections(), topicService.getActualTopicsLimit10BySection());
        } else {
            sectionsAndTopicsDtos = combineListOfSectionsAndTopics(sectionService.getAllSectionsForAnon(), topicService.getActualTopicsLimit10BySectionForAnon());
        }
        return sectionsAndTopicsDtos;
    }
}
