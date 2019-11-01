package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private TopicService topicService;

    @Autowired
    private SectionService sectionService;

    @Test
    void contextLoads() {
    }

    @Test
    void testFor_TopicService_getTopicsLimitAnyBySection() {
    	int expectingLimitLessOrEqual = 1;

        List<Topic> topics = topicService.getTopicsLimitAnyBySection(expectingLimitLessOrEqual);
        Map<Section, List<Topic>> sectionMap = topics.stream().collect(Collectors.groupingBy(Topic::getSection));

        for (Map.Entry<Section, List<Topic>> entry : sectionMap.entrySet()) {
            System.out.println("Section id: " + entry.getKey().getId());
            for (Topic topic : entry.getValue()) {
				System.out.printf("\tTopic id: %s, isHideForAnon: %b, name: %s\n", topic.getId(), topic.isHideToAnon(), topic.getName());
            }
            int size = entry.getValue().size();
            System.out.printf("Topics in section above: %s, expected less or equals to %s\n", size, expectingLimitLessOrEqual);
            Assertions.assertTrue(size <= expectingLimitLessOrEqual);
        }
    }

	@Test
	void testFor_TopicService_getTopicsLimitAnyBySectionForAnon() {
		int expectingLimitLessOrEqual = 1;

		List<Topic> topics = topicService.getTopicsLimitAnyBySectionForAnon(expectingLimitLessOrEqual);
		Map<Section, List<Topic>> sectionMap = topics.stream().collect(Collectors.groupingBy(Topic::getSection));

		for (Map.Entry<Section, List<Topic>> entry : sectionMap.entrySet()) {
			System.out.println("Section id: " + entry.getKey().getId());
			for (Topic topic : entry.getValue()) {
				System.out.printf("\tTopic id: %s, isHideForAnon: %b, name: %s\n", topic.getId(), topic.isHideToAnon(), topic.getName());
				Assertions.assertFalse(topic.isHideToAnon());
			}
			int size = entry.getValue().size();
			System.out.printf("Topics in section above: %s, expected less or equals to %s\n", size, expectingLimitLessOrEqual);
			Assertions.assertTrue(size <= expectingLimitLessOrEqual);
		}
	}

    @Test
	void testFor_SectionsAndTopicsDto() {
		SectionsAndTopicsDto dto = new SectionsAndTopicsDto(sectionService.getAllSections(), topicService.getTopicsLimit10BySection());
		Map<Section, List<Topic>> map = dto.getSectionListMap();
		for (Map.Entry<Section, List<Topic>> entry : map.entrySet()) {
			System.out.println("Section id: " + entry.getKey().getId());
			for (Topic topic : entry.getValue()) {
				System.out.printf("\tTopic id: %s, isHideForAnon: %b, name: %s\n", topic.getId(), topic.isHideToAnon(), topic.getName());
			}
			int size = entry.getValue().size();
			System.out.printf("Topics in section above: %s\n", size);
		}
	}
}
