package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.Collection;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    @Lazy
    private RoleHierarchy roleHierarchy;

    @Autowired
    private UserService userService;

    @Override
    public void createTopic(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public void editTopicByName(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public void deleteTopicById(Long id) {
        topicRepository.deleteById(id);
    }

    @Override
    public Topic findById(Long id) {
        return topicRepository.findById(id).orElse(null);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection) {
        return topicRepository.getActualTopicsLimitAnyBySection(limitTopicsBySection);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection) {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(limitTopicsBySection);
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySection() {
        return topicRepository.getActualTopicsLimitAnyBySection(10);
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySectionForAnon() {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(10);
    }

    @Override
    public Page<Topic> getPageableBySubsection(Section subsection, Pageable pageable) {
        Page<Topic> page;
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authorities);
        if (reachableGrantedAuthorities.contains(new Role("ROLE_USER"))) {
            String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            User user = userService.getUserByNickName(username);
            page = getPageableBySubsectionForUser(user, subsection, pageable);
        } else {
            page = getPageableBySubsectionForAnon(subsection, pageable);
        }

        return page;
    }

    public Page<Topic> getPageableBySubsectionForAnon(Section section, Pageable pageable) {
        return topicRepository.findBySectionAndIsHideToAnonIsFalseOrderByLastMessageTimeDesc(section, pageable);
    }

    @Override
    public Page<Topic> getPageableBySubsectionForUser(User user, Section subsection, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;
        return new PageImpl<>(
                topicRepository.getSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(user.getId(), subsection.getId(), offset, pageSize),
                pageable,
                topicRepository.countForGetSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(user.getId(), subsection.getId())
        );
    }
}
