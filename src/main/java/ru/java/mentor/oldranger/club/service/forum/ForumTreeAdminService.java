package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;

import java.util.List;

public interface ForumTreeAdminService {
    List<SectionsAndTopicsDto> getAllSectionAndAllTopics();
}
