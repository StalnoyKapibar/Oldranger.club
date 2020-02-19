package ru.java.mentor.oldranger.club.service.utils.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.service.utils.TimeParserService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class TimeParserServiceImpl implements TimeParserService {
    @Override
    public LocalDateTime toLocalDateTime(String isoUtcTime) {
        log.info("Get input string in format YYYY-MM-DDTHH:mm:ss.SSSZ - {}", isoUtcTime);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoUtcTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        log.info("Get OffsetDateTime - {}", offsetDateTime);
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        log.info("Get LocalDateTime - {}", localDateTime);
        return localDateTime;
    }

    @Override
    public String toIsoUtcTimeString(LocalDateTime localDateTime) {
        log.info("Get input LocalDateTime - {}", localDateTime);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.UTC);
        log.info("Get OffsetDateTime - {}", offsetDateTime);
        String isoUtcTimeString = DateTimeFormatter.ofPattern("YYYY-MM-DD'T'HH:mm:ss.SSS'Z'").format(offsetDateTime);
        log.info("Set string in format YYYY-MM-DDTHH:mm:ss.SSSZ - {}", isoUtcTimeString);
        return isoUtcTimeString;
    }
}
