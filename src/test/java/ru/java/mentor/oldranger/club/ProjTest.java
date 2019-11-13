package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.projection.IdAndNumberProjection;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.List;

@SpringBootTest
class ProjTest {

	@Autowired
	private TopicService topicService;

	@Autowired
	private UserService userService;

	@Test
	void test1() {
		List<Topic> topics = topicService.findAll();
		List<IdAndNumberProjection> idAndNumberProjections = topicService.getMessagesCountForTopics(topics);
		for (IdAndNumberProjection idAndNumberProjection : idAndNumberProjections) {
			System.out.println(idAndNumberProjection.getId() + " " + idAndNumberProjection.getNumber());
		}
	}

	@Test
	void test2() {
		List<Topic> topics = topicService.findAll();
		User admin = userService.getUserByNickName("admin");
		List<IdAndNumberProjection> idAndNumberProjections = topicService.getNewMessagesCountForTopicsAndUser(topics, admin);
		for (IdAndNumberProjection idAndNumberProjection : idAndNumberProjections) {
			System.out.println(idAndNumberProjection.getId() + " " + idAndNumberProjection.getNumber());
		}
	}

	@Test
	@WithMockUser(value = "admin", username = "admin")
	void test3() {
		List<Topic> topics = topicService.findAll();
		List<TopicAndNewMessagesCountDto> topicsDto = topicService.getTopicsDto(topics);
		for (TopicAndNewMessagesCountDto topicAndNewMessagesCountDto : topicsDto) {
			System.out.println(topicAndNewMessagesCountDto);
		}
	}
}
