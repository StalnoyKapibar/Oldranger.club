package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;
import java.util.Map;

public interface ForumTreeAdminService {
    List<Section> getAllSections();
    void swapSectons(List<Long> sectionsId);
    void swapSubsectons(Map<Long, List<String>> sectionsAndSubsectionsIds);
}
