package ru.java.mentor.oldranger.club.service.user.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.InviteRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InvitationServiceImpl implements InvitationService {
    private InviteRepository repository;

    private static final Logger LOG = LoggerFactory.getLogger(InvitationServiceImpl.class);
    private static final Long MINUTE = 60000L;
    private static final Long HOUR = 3600000L;
    private static final Long DAY_AND_NIGHT = 3600000L;

    private Long shelfLife = DAY_AND_NIGHT;

    @Autowired
    public InvitationServiceImpl(InviteRepository inviteRepository) {
        this.repository = inviteRepository;
    }

    @Override
    public void save(InvitationToken invitationToken) {
        LOG.info("Saving invitation token {}", invitationToken);
        try {
            repository.save(invitationToken);
            LOG.debug("Token saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<InvitationToken> findAll() {
        LOG.debug("Getting invitation tokens");
        List<InvitationToken> tokens = null;
        try {
            tokens = repository.findAll();
            LOG.debug("Returned list of {} tokens", tokens.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return tokens;
    }

    @Override
    public InvitationToken getInvitationTokenByKey(String key) {
        LOG.debug("Getting invitation token");
        InvitationToken token = null;
        try {
            token = repository.findByKey(key);
            LOG.debug("Returned token");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return token;
    }

    @Override
    public void markAsUsed(String key) {
        LOG.info("Marking token as used");
        try {
            InvitationToken token = getInvitationTokenByKey(key);
            token.setUsed(true);
            save(token);
            LOG.info("Token {} marked as used", token);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String getCurrentKey(User user) {
        LOG.info("Getting current key for user with id = {}", user.getId());
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
            LOG.info("Current key = {}", key);
        } catch(Exception e){
            LOG.error(e.getMessage(), e);
        }
            return key;
        }

        @Override
        public boolean checkShelfLife (InvitationToken token){
            String key = token.getKey();
            LocalDateTime date = getDateCreate(key);
            Long elapsedTime = ChronoUnit.MILLIS.between(date, LocalDateTime.now());
            if (elapsedTime >= shelfLife) {
                return true;
            }
            return false;
        }

        @Override
        public void setShelfLife (Long time){
            shelfLife = time;
        }

        @Override
        public String generateKey () {
            LOG.debug("Generating key");
            String key = Math.round((Math.random() * 10000000)) + "" + Math.round((Math.random() * 10000000));
            try {
                while (repository.existsByKey(key)) {
                    key = Math.round((Math.random() * 10000000)) + "" + Math.round((Math.random() * 10000000));
                }
                LOG.info("Key {} generated", key);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            return key;
        }

        @Override
        public LocalDateTime getDateCreate (String key){
        LOG.debug("Getting creation date for key {}", key);
        return getInvitationTokenByKey(key).getDate();
        }

        @Override
        public void markInviteOnMailAsUsed (String mail){
            LOG.info("Marking invite for email {} as used", mail);
            try {
                List<InvitationToken> tokens = repository.findAllByMailAndUsed(mail, false);
                if (tokens.size() != 0) {
                    tokens.get(0).setUsed(true);
                }
                LOG.info("Invite marked as used");
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
