package ru.java.mentor.oldranger.club.service.mail.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.java.mentor.oldranger.club.dao.MailRepository.DirectionRepository;
import ru.java.mentor.oldranger.club.model.mail.Direction;
import ru.java.mentor.oldranger.club.model.mail.DirectionType;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.mail.MailDirectionService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@EnableScheduling
@Service
public class MailDirectionServiceImpl implements MailDirectionService {
    private static final long scheduledTime = 3600000;
    private List<Direction> directions;

    private TopicService service;

    private UserService userService;

    private ChatRepository chatRepository;

    private DirectionRepository directionRepository;

    private MailService mailService;

    @Scheduled(fixedRate = scheduledTime)
    @Override
    public void sendDirection() {
        LocalDateTime nowTime = LocalDateTime.now();

        directions = directionRepository.findAll();
        for (Direction direction : directions) {
            String email = direction.getUser().getEmail();
            switch (direction.getDirectionType()) {
                case ONE_TO_DAY:
                    if (nowTime.getDayOfMonth() != direction.getLastSendTime().getDayOfMonth() &&
                            nowTime.getHour() > 8) {
                        direction.setLastSendTime(nowTime);
                        mailService.sendHtmlMessage(email,getCountTopicsAndActiveChats(email), "directionLetter.html");
                    }
                    break;
                case TWO_TO_DAY:
                    if (Math.abs(nowTime.getHour() - direction.getLastSendTime().getHour()) > 12) {
                        direction.setLastSendTime(nowTime);
                        mailService.sendHtmlMessage(email,getCountTopicsAndActiveChats(email), "directionLetter.html");
                    }
                    break;
                case ONE_TO_WEEK:
                    if (nowTime.getDayOfWeek().equals(direction.getLastSendTime().getDayOfWeek()) && nowTime.getHour() == 8) {
                        mailService.sendHtmlMessage(email,getCountTopicsAndActiveChats(email), "directionLetter.html");
                    }
            }
        }
    }
    // Maybe need rework.
    public Map<String, Object> getCountTopicsAndActiveChats(String email) {
        Map<String, Object> countTopicsAndActiveChats = new HashMap<>();
        User user = userService.getUserByEmail(email);
        Integer newTopicsCount;
        try {
            newTopicsCount = service
                    .getNewMessagesCountForTopicsAndUser(service
                            .findAll(), user).size();
        }catch (NullPointerException e){
            newTopicsCount = 0;
        }
        Integer countActiveChats;
        try {
            countActiveChats = chatRepository.getCountChatByUserID(user.getId());
        }catch (NullPointerException e){
            countActiveChats = 0;
        }

        countTopicsAndActiveChats.put("unreadTopics", newTopicsCount);
        countTopicsAndActiveChats.put("activeChats", countActiveChats);
        return countTopicsAndActiveChats;
    }

    @Override
    public void changeUserDirection(Long userId, DirectionType type) {
        User user = userService.findById(userId);
        Direction direction = new Direction();
        direction.setDirectionType(type);
        direction.setUser(user);
        direction.setLastSendTime(LocalDateTime.now());
        directionRepository.save(direction);
    }
}
