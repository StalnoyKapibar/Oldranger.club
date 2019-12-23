package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.*;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SectionsAndTopicsServiceImpl implements SectionsAndTopicsService {

    private RoleHierarchy roleHierarchy;

    private SectionService sectionService;

    private TopicService topicService;

    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;

    private SearchService searchService;

    public List<SectionsAndTopicsDto> getAllSectionsAndActualTopicsLimit10BySection() {
        List<SectionsAndTopicsDto> sectionsAndTopicsDtos;
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        if (reachableGrantedAuthorities.contains(new Role("ROLE_USER"))) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            sectionsAndTopicsDtos = combineListOfSectionsAndTopicsSortSubscriptionsFirst(sectionService.getAllSections(), topicService.getActualTopicsLimit10BySection(), user);
        } else {
            sectionsAndTopicsDtos = combineListOfSectionsAndTopics(sectionService.getAllSectionsForAnon(), topicService.getActualTopicsLimit10BySectionForAnon());
        }
        return sectionsAndTopicsDtos;
    }

    private List<SectionsAndTopicsDto> combineListOfSectionsAndTopics(List<Section> sections, List<Topic> topics) {
        List<SectionsAndTopicsDto> dtos = new ArrayList<>();
        for (Section section : sections) {
            List<Topic> topicList = topics.stream().filter(topic -> topic.getSection().equals(section)).collect(Collectors.toList());
            SectionsAndTopicsDto dto = new SectionsAndTopicsDto(section, topicList);
            dtos.add(dto);
        }
        return dtos;
    }

    private List<SectionsAndTopicsDto> combineListOfSectionsAndTopicsSortSubscriptionsFirst(List<Section> sections, List<Topic> topics, User user) {
        List<SectionsAndTopicsDto> dtos = new ArrayList<>();
        List<TopicVisitAndSubscription> subscriptionsForUser = topicVisitAndSubscriptionService.getTopicVisitAndSubscriptionForUser(user);
        for (Section section : sections) {
            List<Topic> topicList = topics
                    .stream()
                    .filter(topic -> topic.getSection().equals(section))
                    .sorted(new TopicsHasUserSubscriptionFirst(subscriptionsForUser))
                    .collect(Collectors.toList());
            SectionsAndTopicsDto dto = new SectionsAndTopicsDto(section, topicList);
            dtos.add(dto);
        }
        return dtos;
    }

    private static class TopicsHasUserSubscriptionFirst implements Comparator<Topic> {

        private List<TopicVisitAndSubscription> subscriptionsForUser;

        TopicsHasUserSubscriptionFirst(List<TopicVisitAndSubscription> subscriptionsForUser) {
            this.subscriptionsForUser = subscriptionsForUser;
        }

        @Override
        public int compare(Topic o1, Topic o2) {
            boolean o1HasSubscription = subscriptionsForUser.stream().anyMatch(subscription -> subscription.getTopic().equals(o1));
            boolean o2HasSubscription = subscriptionsForUser.stream().anyMatch(subscription -> subscription.getTopic().equals(o2));
            return Boolean.compare(o2HasSubscription, o1HasSubscription);
        }
    }

    @Deprecated
    public List<Topic> getTopicsByQuery(String query, String searchBy) {
        if (searchBy.equals("byTopics")) {
            return searchService.searchByTopicName(query);
        } else {
            List<Comment> comments = searchService.searchByComment(query, null, null);
            List<Topic> topics = new ArrayList<>();
            comments.forEach(comment -> topics.add(comment.getTopic()));
            return topics;
        }
    }

    @Deprecated
    public List<SectionsAndTopicsDto> getSectionsAndTopicsByQuery(String query, String searchBy) {
        List<SectionsAndTopicsDto> sectionsAndTopicsDtos;
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        List<Topic> topics = getTopicsByQuery(query, searchBy);
        if (reachableGrantedAuthorities.contains(new Role("ROLE_USER"))) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            sectionsAndTopicsDtos = combineListOfSectionsAndTopicsSortSubscriptionsFirst(sectionService.getAllSections(), topics, user);
        } else {
            List<Topic> topicsForAnon = new ArrayList<>();
            for (Topic topic : topics) {
                if (!topic.isHideToAnon())
                    topicsForAnon.add(topic);
            }
            sectionsAndTopicsDtos = combineListOfSectionsAndTopics(sectionService.getAllSectionsForAnon(), topicsForAnon);
        }
        sectionsAndTopicsDtos.removeIf(sectionsAndTopicsDto -> sectionsAndTopicsDto.getTopics().isEmpty());
        return sectionsAndTopicsDtos;
    }
}