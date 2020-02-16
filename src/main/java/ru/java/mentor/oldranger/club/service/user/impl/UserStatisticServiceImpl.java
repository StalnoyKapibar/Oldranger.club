package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserStaticRepository;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserStatisticServiceImpl implements UserStatisticService {

    private UserStaticRepository userStaticRepository;

    @Override
    public UserStatistic getUserStaticById(Long id) {
        log.debug("Getting statistic for user with id = {}", id);
        UserStatistic statistic = null;
        try {
            statistic = userStaticRepository.getOne(id);
            log.debug("User statistic returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return statistic;
    }

    public UserStatistic getUserStaticByUser(User user) {
        log.debug("Getting statistic for user with id = {}", user.getId());
        UserStatistic statistic = null;
        try {
            statistic = userStaticRepository.getOneByUser(user);
            log.debug("User statistic returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return statistic;
    }

    @Override
    public void saveUserStatic(UserStatistic userStatistic) {
        log.info("Saving user statistic");
        try {
            userStaticRepository.save(userStatistic);
            log.info("User statistic saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Page<UserStatistic> getAllUserStatistic(Pageable pageable) {
        log.debug("Getting page {} of user statistic", pageable.getPageNumber());
        Page<UserStatistic> page = null;
        try {
            page = userStaticRepository.findAll(pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public Page<UserStatistic> getUserStatisticsByQuery(Pageable pageable, String query) {
        log.debug("Getting page {} of user statistic by query = {}", pageable.getPageNumber(), query);
        Page<UserStatistic> page = null;
        try {
            page = userStaticRepository.findByQuery(pageable, query);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public List<UserStatisticDto> getUserStatisticDtoFromUserStatistic(List<UserStatistic> users) {
        log.debug("Building user statistic dto");
        List<UserStatisticDto> dtos = new ArrayList<>();
        users.forEach(user -> dtos.add(new UserStatisticDto(user.getUser().getId(),
                user.getUser().getNickName(),
                user.getUser().getEmail(),
                user.getUser().getRegDate(),
                user.getUser().getRole().getAuthority(),
                user.getLastComment(),
                user.getLastVizit())));
        return dtos;
    }
}