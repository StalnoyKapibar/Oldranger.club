package ru.java.mentor.oldranger.club.service.user.impl;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.InviteRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    @NonNull
    private InviteRepository repository;

    private static final Long MINUTE = 60000L;
    private static final Long HOUR = 3600000L;
    private static final Long DAY_AND_NIGHT = 3600000L;

    private Long shelfLife = DAY_AND_NIGHT;

    @Override
    public void save(InvitationToken invitationToken) {
        log.info("Saving invitation token {}", invitationToken);
        try {
            repository.save(invitationToken);
            log.debug("Token saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<InvitationToken> findAll() {
        log.debug("Getting invitation tokens");
        List<InvitationToken> tokens = null;
        try {
            tokens = repository.findAll();
            log.debug("Returned list of {} tokens", tokens.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tokens;
    }

    @Override
    public InvitationToken getInvitationTokenByKey(String key) {
        log.debug("Getting invitation token");
        InvitationToken token = null;
        try {
            token = repository.findByKey(key);
            log.debug("Returned token");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return token;
    }

    @Override
    public void markAsUsed(String key) {
        log.info("Marking token as used");
        try {
            InvitationToken token = getInvitationTokenByKey(key);
            token.setUsed(true);
            save(token);
            log.info("Token {} marked as used", token);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getCurrentKey(User user) {
        log.info("Getting current key for user with id = {}", user.getId());
        String key = null;
        try {
            List<InvitationToken> tokens = repository.findAllByUserAndUsedAndMail(user, false, null);
            if (tokens.size() != 0) {
                key = tokens.get(0).getKey();
                if (checkShelfLife(tokens.get(0))) {
                    markAsUsed(key);
                    key = generateKey();
                    save(new InvitationToken(key, user));
                }
            }
            log.info("Current key = {}", key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return key;
    }

    @Override
    public boolean checkShelfLife(InvitationToken token) {
        String key = token.getKey();
        LocalDateTime date = getDateCreate(key);
        Long elapsedTime = ChronoUnit.MILLIS.between(date, LocalDateTime.now());
        return elapsedTime >= shelfLife;
    }

    @Override
    public void setShelfLife(Long time) {
        shelfLife = time;
    }

    @Override
    public String generateKey() {
        log.debug("Generating key");
        String key = Math.round((Math.random() * 10000000)) + "" + Math.round((Math.random() * 10000000));
        try {
            while (repository.existsByKey(key)) {
                key = Math.round((Math.random() * 10000000)) + "" + Math.round((Math.random() * 10000000));
            }
            log.info("Key {} generated", key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return key;
    }

    @Override
    public String generateMD5Key(String email) {
        return DigestUtils.md5Hex(email);
    }

    @Override
    public LocalDateTime getDateCreate(String key) {
        log.debug("Getting creation date for key {}", key);
        return getInvitationTokenByKey(key).getDate();
    }

    @Override
    public void markInviteOnMailAsUsed(String mail) {
        log.info("Marking invite for email {} as used", mail);
        try {
            List<InvitationToken> tokens = repository.findAllByMailAndUsed(mail, false);
            if (tokens.size() != 0) {
                tokens.get(0).setUsed(true);
                save(tokens.get(0));
            }
            log.info("Invite marked as used");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
