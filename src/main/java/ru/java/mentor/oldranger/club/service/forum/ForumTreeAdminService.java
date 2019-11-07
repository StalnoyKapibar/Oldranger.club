package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;

import java.util.List;
import java.util.Map;

public interface ForumTreeAdminService {
    List<SectionsAndTopicsDto> getAllSectionAndAllTopics();
    void swapSectons(Map<String, Long> swapSections);
}
