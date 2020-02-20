package ru.java.mentor.oldranger.club.service.forum.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
class TopicServiceImplTest {

    private TopicServiceImpl topicService;

    @Mock
    private UserStatisticService userStatisticService = Mockito.mock(UserStatisticService.class);

    @Mock
    private SecurityUtilsService securityUtilsService = Mockito.mock(SecurityUtilsService.class);

    @Mock
    private TopicRepository topicRepository = Mockito.mock(TopicRepository.class);

    @Mock
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService = Mockito.mock(TopicVisitAndSubscriptionService.class);

    @Mock
    private Pageable pageable = Mockito.mock(Pageable.class);

    @BeforeEach
    void initAlgo() {
        topicService = new TopicServiceImpl(topicRepository, userStatisticService, securityUtilsService, topicVisitAndSubscriptionService);
    }

    @Test
    public void createTopic() {
        User user = new User("String firstName", "String lastName", "String email", "String nickName", null);
        Topic topic = new Topic("String name", user, LocalDateTime.now(), null, null, true, false);
        UserStatistic userStatistic = new UserStatistic(user);
        Mockito.when(userStatisticService.getUserStaticByUser(topic.getTopicStarter())).thenReturn(userStatistic);
        topicService.createTopic(topic);
        Mockito.verify(userStatisticService, Mockito.times(1)).saveUserStatic(userStatistic);
        Mockito.verify(topicRepository, Mockito.times(1)).save(topic);
    }

    @Test
    public void getPageableBySubsectionForUser() {
        User user = new User("String firstName", "String lastName", "String email", "String nickName", null);
        user.setId(1L);
        Section section = new Section("String name", 1, true);
        Subsection subsection = new Subsection("String name", 1, section, true);
        subsection.setId(1L);
        topicService.getPageableBySubsectionForUser(user, subsection, pageable);
        Mockito.verify(topicRepository, Mockito.times(1)).getSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(topicRepository, Mockito.times(1)).countForGetSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void getTopicsDto() {
        User user = new User("String firstName", "String lastName", "String email", "String nickName", new Role("ROLE_USER"));
        user.setId(1L);
        Topic topic = new Topic("String name", user, LocalDateTime.now(), null, null, true, false);
        List<Topic> topics = new ArrayList<>();
        topics.add(topic);
        Mockito.when(securityUtilsService.isLoggedUserIsUser()).thenReturn(true);
        Mockito.when(securityUtilsService.getLoggedUser()).thenReturn(user);
        List<TopicAndNewMessagesCountDto> dtos = topicService.getTopicsDto(topics);
        Mockito.verify(topicVisitAndSubscriptionService, Mockito.times(1)).getTopicVisitAndSubscriptionForUser(user);
        Assert.assertEquals(dtos.get(0).getTopic(), topic);
        Assert.assertEquals(dtos.get(0).getTopic().getName(), "String name");
        Assert.assertEquals(0, dtos.get(0).getTotalMessages());
        Assert.assertFalse(dtos.get(0).getIsSubscribed());
        Assert.assertFalse(dtos.get(0).getHasNewMessages());
        Assert.assertEquals((Long) 0L, dtos.get(0).getNewMessagesCount());
    }
}