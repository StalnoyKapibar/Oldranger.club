package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import ru.java.mentor.oldranger.club.model.forum.Theme;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@ContextConfiguration
class SectionsAndTopicsTests {

    private final int EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS = 2;

    @Autowired
    private TopicService topicService;
    @Autowired
    private SectionsAndTopicsService sectionsAndTopicsService;
    @Autowired
    private RoleHierarchy roleHierarchy;

    @Test
    void testFor_TopicService_getTopicsLimitAnyBySection() {
        List<Topic> topics = topicService.getActualTopicsLimitAnyBySection(EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS);
        Map<Section, List<Topic>> sectionMap = topics.stream().collect(Collectors.groupingBy(Topic::getSection));

        for (Map.Entry<Section, List<Topic>> entry : sectionMap.entrySet()) {
            printSection(entry.getKey());
            for (Topic topic : entry.getValue()) {
                printTopic(topic);
            }
            int size = entry.getValue().size();
            printTopicsAbove(size, EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS);
            Assertions.assertTrue(size <= EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS);
        }
    }

    @Test
    void testFor_TopicService_getTopicsLimitAnyBySectionForAnon() {
        List<Topic> topics = topicService.getActualTopicsLimitAnyBySectionForAnon(EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS);
        Map<Section, List<Topic>> sectionMap = topics.stream().collect(Collectors.groupingBy(Topic::getSection));

        for (Map.Entry<Section, List<Topic>> entry : sectionMap.entrySet()) {
            printSection(entry.getKey());
            for (Topic topic : entry.getValue()) {
                printTopic(topic);
                Assertions.assertFalse(topic.isHideToAnon());
            }
            int size = entry.getValue().size();
            printTopicsAbove(size, EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS);
            Assertions.assertTrue(size <= EXPECTING_TOPICS_LIMIT_LESS_OR_EQUALS);
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFor_SectionsAndTopicsController_ADMIN() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);

        System.out.println("authorities = " + authorities);
        System.out.println("reachableGrantedAuthorities = " + reachableGrantedAuthorities);

        List<SectionsAndTopicsDto> dtos = sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection();
        for (SectionsAndTopicsDto dto : dtos) {
            printSection(dto.getSection());
            for (Topic topic : dto.getTopics()) {
                printTopic(topic);
            }
            int size = dto.getTopics().size();
            printTopicsAbove(size);
        }
    }

    @Test
    @WithAnonymousUser
    void testFor_SectionsAndTopicsController_ANONYMOUS() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);

        System.out.println("authorities = " + authorities);
        System.out.println("reachableGrantedAuthorities = " + reachableGrantedAuthorities);

        List<SectionsAndTopicsDto> dtos = sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection();
        for (SectionsAndTopicsDto dto : dtos) {
            Section section = dto.getSection();
            printSection(section);
            Assertions.assertFalse(section.isHideToAnon());
            for (Topic topic : dto.getTopics()) {
                printTopic(topic);
                Assertions.assertFalse(topic.isHideToAnon());
            }
            int size = dto.getTopics().size();
            printTopicsAbove(size);
        }
    }

    private void printTopic(Topic topic) {
        System.out.println(describeTopic(topic));
    }

    private void printSection(Section section) {
        System.out.println(describeSection(section));
    }

    private void printTopicsAbove(int size) {
        System.out.println(describeTopicsAbove(size));
    }

    private void printTopicsAbove(int size, int expectingSize) {
        System.out.println(describeTopicsAbove(size, expectingSize));
    }

    private String describeTopicsAbove(int size) {
        return "Topics in section above: " + size;
    }

    private String describeTopicsAbove(int size, int expectingSize) {
        return String.format("Topics in section above: %s, expected less or equals to %s", size, expectingSize);
    }

    private String describeTopic(Topic topic) {
        return String.format("\tTopic id: %s, isHideForAnon: %b, name: %s", topic.getId(), topic.isHideToAnon(), topic.getName());
    }

    private String describeSection(Section section) {
        return String.format("Section id: %s, isHideForAnon: %b, name: %s", section.getId(), section.isHideToAnon(), section.getName());
    }

}
