package ru.oldranger.club.service.mail.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.ForumRepository.DirectionRepository;
import ru.oldranger.club.model.chat.Chat;
import ru.oldranger.club.model.mail.Direction;
import ru.oldranger.club.model.mail.DirectionType;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.chat.ChatService;
import ru.oldranger.club.service.forum.TopicService;
import ru.oldranger.club.service.mail.MailDirectionService;
import ru.oldranger.club.service.mail.MailService;
import ru.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@EnableScheduling
@Service
public class MailDirectionServiceImpl implements MailDirectionService {
    private final long scheduleTime = 3600000; // время в мс. 1ч == 3600000.

    private UserService userService;
    private ChatService chatService;
    private MailService mailService;
    private DirectionRepository directionRepository;
    private TopicService topicService;

    @Override
    @Scheduled(fixedRate = scheduleTime)
    public void sendDirection() {
        LocalDateTime nowTime = LocalDateTime.now();
        List<Direction> directions;
        try {
            directions = directionRepository.findAll();
        } catch (NullPointerException e) {
            return;
        }
        for (Direction direction : directions) {
            String email = direction.getUser().getEmail();
            switch (direction.getDirectionType()) {
                case ONE_TO_DAY:
                    if (nowTime.getDayOfMonth() != direction.getLastSendTime().getDayOfMonth() &&
                            nowTime.getHour() > 8) {
                        direction.setLastSendTime(nowTime);
                        mailService.sendHtmlMessage(email, getCountTopicsAndActiveChats(email), "directionLetter.html");
                        directionRepository.save(direction);
                    }
                    break;
                case TWO_TO_DAY:
                    if (Math.abs(nowTime.getHour() - direction.getLastSendTime().getHour()) > 12) {
                        direction.setLastSendTime(nowTime);
                        mailService.sendHtmlMessage(email, getCountTopicsAndActiveChats(email), "directionLetter.html");
                        directionRepository.save(direction);
                    }
                    break;
                case ONE_TO_WEEK:
                    if (nowTime.getDayOfWeek().equals(direction.getLastSendTime().getDayOfWeek()) && nowTime.getHour() == 8) {
                        mailService.sendHtmlMessage(email, getCountTopicsAndActiveChats(email), "directionLetter.html");
                        directionRepository.save(direction);
                    }
            }
        }
    }

    @Override
    public void changeUserDirection(User user, DirectionType type) {
        Direction direction = directionRepository.getByUser(user);
        if (direction == null) {
            direction = new Direction();
            direction.setLastSendTime(LocalDateTime.now());
            direction.setUser(user);
        }
        direction.setDirectionType(type);
        directionRepository.save(direction);
    }

    private Map<String, Object> getCountTopicsAndActiveChats(String email) {
        User user = userService.getUserByEmail(email);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("unreadTopics", topicService.getNewMessagesCountForTopicsAndUser(topicService.findAll(), user).size());
        List<Chat> chats = chatService.getAllPrivateChats(user);
        attributes.put("activeChats", chats.size());
        return attributes;
    }
}
