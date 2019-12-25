package ru.java.mentor.oldranger.club.service.user.impl;


import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private InviteRepository repository;

    private static final Long MINUTE = 60000L;
    private static final Long HOUR = 3600000L;
    private static final Long DAY_AND_NIGHT = 3600000L;

    private Long shelfLife = DAY_AND_NIGHT;

    @Override
    public void save(InvitationToken invitationToken) {
        repository.save(invitationToken);
    }

    @Override
    public List<InvitationToken> findAll() {
        return repository.findAll();
    }

    @Override
    public InvitationToken getInvitationTokenByKey(String key) {
        return repository.findByKey(key);
    }

    @Override
    public void markAsUsed(String key) {
        InvitationToken token = getInvitationTokenByKey(key);
        token.setUsed(true);
        save(token);
    }

    @Override
    public String getCurrentKey(User user) {
        List<InvitationToken> tokens = repository.findAllByUserAndUsedAndMail(user, false, null);
        if (tokens.size() == 0) {
            return null;
        } else {
            String key = tokens.get(0).getKey();
            if (checkShelfLife(tokens.get(0))) {
                markAsUsed(key);
                key = generateKey();
                save(new InvitationToken(key, user));
            }
            return key;
        }

    }

    @Override
    public boolean checkShelfLife(InvitationToken token) {
        String key = token.getKey();
        LocalDateTime date = getDateCreate(key);
        Long elapsedTime = ChronoUnit.MILLIS.between(date, LocalDateTime.now());
        if (elapsedTime >= shelfLife) {
            return true;
        }
        return false;
    }

    @Override
    public void setShelfLife(Long time) {
        shelfLife = time;
    }

    @Override
    public String generateKey() {
        String key = Math.round((Math.random() * 10000000)) + "" + Math.round((Math.random() * 10000000));
        while (repository.existsByKey(key)) {
            key = Math.round((Math.random() * 10000000)) + "" + Math.round((Math.random() * 10000000));
        }
        return key;
    }

    @Override
    public LocalDateTime getDateCreate(String key) {
        return getInvitationTokenByKey(key).getDate();
    }

    @Override
    public void markInviteOnMailAsUsed(String mail) {
        List<InvitationToken> tokens = repository.findAllByMailAndUsed(mail, false);
        if (tokens.size() != 0) {
            tokens.get(0).setUsed(true);
        }
    }
}
