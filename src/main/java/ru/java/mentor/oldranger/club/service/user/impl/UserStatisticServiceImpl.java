package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@AllArgsConstructor
public class UserStatisticServiceImpl implements UserStatisticService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserStaticRepository userStaticRepository;

    @Override
    public UserStatistic getUserStaticById(Long id) {
        LOG.debug("Getting statistic for user with id = {}", id);
        UserStatistic statistic = null;
        try {
            statistic = userStaticRepository.getOne(id);
            LOG.debug("User statistic returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return statistic;
    }

    public UserStatistic getUserStaticByUser(User user) {
        LOG.debug("Getting statistic for user with id = {}", user.getId());
        UserStatistic statistic = null;
        try {
            statistic = userStaticRepository.getOneByUser(user);
            LOG.debug("User statistic returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return statistic;
    }

    @Override
    public void saveUserStatic(UserStatistic userStatistic) {
        LOG.info("Saving user statistic");
        try {
            userStaticRepository.save(userStatistic);
            LOG.info("User statistic saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Page<UserStatistic> getAllUserStatistic(Pageable pageable) {
        LOG.debug("Getting page {} of user statistic", pageable.getPageNumber());
        Page<UserStatistic> page = null;
        try {
            page = userStaticRepository.findAll(pageable);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public Page<UserStatistic> getUserStatisticsByQuery(Pageable pageable, String query) {
        LOG.debug("Getting page {} of user statistic by query = {}", pageable.getPageNumber(), query);
        Page<UserStatistic> page = null;
        try {
            page = userStaticRepository.findByQuery(pageable, query);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public List<UserStatisticDto> getUserStatisticDtoFromUserStatistic(List<UserStatistic> users) {
        LOG.debug("Building user statistic dto");
        List<UserStatisticDto> dtos = new ArrayList<>();
        users.forEach(user -> dtos.add(new UserStatisticDto(user.getUser().getNickName(),
                user.getUser().getEmail(),
                user.getUser().getRegDate(),
                user.getUser().getRole().getAuthority(),
                user.getLastComment(),
                user.getLastVizit())));
        return dtos;
    }
}