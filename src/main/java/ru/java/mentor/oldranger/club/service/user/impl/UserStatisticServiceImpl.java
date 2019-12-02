package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
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

    private UserStaticRepository userStaticRepository;

    @Override
    public UserStatistic getUserStaticById(Long id) {
        return userStaticRepository.getOne(id);
    }

    public UserStatistic getUserStaticByUser(User user) {
        return userStaticRepository.getOneByUser(user);
    }

    @Override
    public void saveUserStatic(UserStatistic userStatistic) {
        userStaticRepository.save(userStatistic);
    }

    @Override
    public Page<UserStatistic> getAllUserStatistic(Pageable pageable) {
        return  userStaticRepository.findAll(pageable);
    }

    @Override
    public Page<UserStatistic> getUserStatisticsByQuery(Pageable pageable, String query) {
        return  userStaticRepository.findByQuery(pageable, query);
    }

    @Override
    public List<UserStatisticDto> getUserStatisticDtoFromUserStatistic(List<UserStatistic> users) {
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