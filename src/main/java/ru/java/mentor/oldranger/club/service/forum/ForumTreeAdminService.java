package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;

public interface ForumTreeAdminService {
    List<Section> getAllSections();
    void swapSectons(List<Long> sectionsId);
}
