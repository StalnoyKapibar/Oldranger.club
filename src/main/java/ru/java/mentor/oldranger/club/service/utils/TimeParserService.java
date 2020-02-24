package ru.java.mentor.oldranger.club.service.utils;
import java.time.LocalDateTime;


public interface TimeParserService {
    LocalDateTime toLocalDateTime(String isoUtcTime);
    String toIsoUtcTimeString(LocalDateTime localDateTime);
}
