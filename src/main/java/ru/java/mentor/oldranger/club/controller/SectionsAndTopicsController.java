package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.Collection;

@Controller
@AllArgsConstructor
public class SectionsAndTopicsController {

    private RoleHierarchy roleHierarchy;

    private SectionService sectionService;

    private TopicService topicService;

    public SectionsAndTopicsDto getAllSectionsAndActualTopicsLimit10BySection() {
        SectionsAndTopicsDto sectionsAndTopicsDto;
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        if (reachableGrantedAuthorities.contains(new Role("ROLE_USER"))) {
            sectionsAndTopicsDto = new SectionsAndTopicsDto(sectionService.getAllSections(), topicService.getActualTopicsLimit10BySection());
        } else {
            sectionsAndTopicsDto = new SectionsAndTopicsDto(sectionService.getAllSectionsForAnon(), topicService.getActualTopicsLimit10BySectionForAnon());
        }
        return sectionsAndTopicsDto;
    }
}
