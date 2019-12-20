package ru.java.mentor.oldranger.club.dao.SearchRepository;

import java.util.List;

public interface SearchRepository {
    List searchObjectsByName(String finderTag, String[] fetchingFields,String[] targetFields, Class aClass);
}
