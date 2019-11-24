package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;

import java.util.List;

public interface SectionsAndTopicsService {
    List<SectionsAndTopicsDto> getAllSectionsAndActualTopicsLimit10BySection();

    List<SectionsAndTopicsDto> getSectionsAndTopicsByQuery(String query, String searchBy);
}