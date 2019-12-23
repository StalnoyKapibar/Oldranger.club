package ru.java.mentor.oldranger.club.dao.SearchRepository;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface SearchRepository {
    List searchObjectsByName(String finderTag, String[] fetchingFields, @NotNull String[] targetFields, Class aClass);
}
