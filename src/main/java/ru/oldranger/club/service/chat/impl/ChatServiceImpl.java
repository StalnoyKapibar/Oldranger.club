package ru.oldranger.club.service.chat.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.oldranger.club.model.chat.Chat;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.chat.ChatService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private ChatRepository chatRepository;

    @Override
    public Chat getChatByToken(String token) {
        log.debug("Getting chat by token {}.", token);
        Chat chat = null;
        try {
            chat = chatRepository.findChatByToken(token);
            log.debug("Returned chat with id = {}", chat.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return chat;
    }

    @Override
    public Chat getChatById(Long id) {
        log.debug("Getting chat by id {}.", id);
        Chat chat = null;
        try {
            chat = chatRepository.findChatById(id);
            log.debug("Chat returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return chat;
    }

    @Override
    public void createChat(Chat chat) {
        log.info("Saving chat {}", chat);
        try {
            chatRepository.save(chat);
            log.info("Chat saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Chat> getAllChats() {
        log.debug("Getting all chats");
        List<Chat> chats = null;
        try {
            chats = chatRepository.findAll();
            log.debug("Returned list of {} chats", chats.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return chats;
    }

    @Override
    public Chat getGroupChat() {
        log.debug("Getting group chat");
        Chat chat = null;
        try {
            chat = chatRepository.findGroupChat();
            log.debug("Returned group chat");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return chat;
    }

    @Override
    public String generateToken(User first, User second) {
        return first.getId() + "_@_" + UUID.randomUUID().toString() + "_@_" + second.getId();
    }

    @Override
    public List<Chat> getAllPrivateChats(User user) {
        log.debug("Getting all private chats of user with id = {}", user.getId());
        List<Chat> chats = null;
        try {
            chats = chatRepository.findAllByPrivacyTrueAndUserListContaining(user);
            log.debug("Returned list of {} chats", chats.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return chats;
    }
}