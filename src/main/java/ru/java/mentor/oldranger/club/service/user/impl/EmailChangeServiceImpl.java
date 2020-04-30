package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.EmailChangeRepository;
import ru.java.mentor.oldranger.club.model.user.EmailChangeToken;
import ru.java.mentor.oldranger.club.service.user.EmailChangeService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailChangeServiceImpl implements EmailChangeService {
    @NonNull
    EmailChangeRepository emailChangeRepository;

    @Override
    public void save(EmailChangeToken emailChangeToken) {
        log.info("Saving email change dto {}", emailChangeToken);
        try {
            emailChangeRepository.save(emailChangeToken);
            log.debug("Dto saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public EmailChangeToken getEmailChangeDtoByKey(String key) {
        log.debug("Getting email change dto from key - {}", key);
        EmailChangeToken emailChangeToken = null;
        try {
            emailChangeToken = emailChangeRepository.findByKey(key);
            log.debug("Email change dto was found!");
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return emailChangeToken;
    }

    @Override
    public String generateMD5Key(String newEmail) {
        return DigestUtils.md5Hex(newEmail);
    }
}
