package ru.java.mentor.oldranger.club.service.chat.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceImpl.class);
    private ChatRepository chatRepository;

    @Override
    public Chat getChatByToken(String token) {
        LOG.debug("Getting chat by token {}.", token);
        Chat chat = null;
        try {
            chat = chatRepository.findChatByToken(token);
            LOG.debug("Returned chat with id = {}", chat.getId());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return chat;
    }

    @Override
    public Chat getChatById(Long id) {
        LOG.debug("Getting chat by id {}.", id);
        Chat chat = null;
        try {
            chat = chatRepository.findChatById(id);
            LOG.debug("Chat returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return chat;
    }

    @Override
    public void createChat(Chat chat) {
        LOG.info("Saving chat {}", chat);
        try {
            chatRepository.save(chat);
            LOG.info("Chat saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Chat> getAllChats() {
        LOG.debug("Getting all chats");
        List<Chat> chats = null;
        try {
            chats = chatRepository.findAll();
            LOG.debug("Returned list of {} chats", chats.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return chats;
    }

    @Override
    public String generateToken(User first, User second) {
        return first.getId() + "_@_" + UUID.randomUUID().toString() + "_@_" + second.getId();
    }

    @Override
    public List<Chat> getAllPrivateChats(User user) {
        LOG.debug("Getting all private chats of user with id = {}", user.getId());
        List<Chat> chats = null;
        try {
            chats = chatRepository.findAllByPrivacyTrueAndUserListContaining(user);
            LOG.debug("Returned list of {} chats", chats.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return chats;
    }
}