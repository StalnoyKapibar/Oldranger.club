package ru.java.mentor.oldranger.club.service.chat.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.ChatRepository.MessageRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);
    private MessageRepository messageRepository;
    private ChatService chatService;
    private String uploadDir;
    private String olderThan;

    public MessageServiceImpl(MessageRepository messageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        uploadDir = "./uploads/chat";
        olderThan = "week";
    }

    public void setOlderThan(String olderThan) {
        this.olderThan = olderThan;
    }

    @Override
    public Message findFirstMessageByChat(Chat chat) {
        LOG.debug("Getting oldest message of chat with id = {}", chat.getId());
        Message message = null;
        try {
            message = messageRepository.findFirstByChatOrderByMessageDateAsc(chat);
            LOG.debug("Returned message with id = {}", message.getId());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return message;
    }

    @Override
    public void addMessage(Message message) {
        LOG.info("Saving message {}", message);
        try {
            messageRepository.save(message);
            LOG.info("Message saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void removeMessageById(Long id) {
        LOG.info("Deleting message with id = {}", id);
        try {
            messageRepository.deleteById(id);
            LOG.info("Message deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Page<Message> getPagebleMessages(Chat chat, Pageable pageable) {
        LOG.debug("Getting page {} of messages for chat with id = {}", pageable.getPageNumber(), chat.getId());
        Page<Message> page = null;
        try {
            page = messageRepository.findAllByChat(chat, pageable);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }

    private String uploadImage(MultipartFile file) throws IOException {
        LOG.info("Uploading file {}", file.getOriginalFilename());
        File uploadPath = new File(uploadDir);
        String resultFileName;
        try {
            if (!uploadPath.exists()) {
                uploadPath.mkdir();
            }
            resultFileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
            Path copyLocation = Paths
                    .get(uploadDir + File.separator + resultFileName);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            LOG.info("File uploaded");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return resultFileName;
    }

    private String thumbnailImage(String imageName, MultipartFile file) throws IOException {
        LOG.info("Thumbnailing file {}", file.getOriginalFilename());
        String resultFileName = null;
        try {
            resultFileName = UUID.randomUUID().toString() + "__" +
                    150 + "px" + StringUtils.cleanPath(file.getOriginalFilename());
            String resultFileLocation = Paths
                    .get(uploadDir + File.separator + resultFileName).toString();
            Thumbnails.of(uploadDir + File.separator + imageName)
                    .size(150, 150)
                    .toFile(resultFileLocation);
            LOG.info("Image thumbnailed");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return resultFileName;
    }

    @Override
    public Map<String, String> processImage(MultipartFile file) {
        LOG.info("Processing file {}", file.getOriginalFilename());
        if (("image/jpeg").equals(file.getContentType()) || ("image/png").equals(file.getContentType())) {
            Map<String, String> avatars = new HashMap<>();
            try {
                avatars.put("originalImg", uploadImage(file));
                avatars.put("thumbnailImg", thumbnailImage(avatars.get("originalImg"), file));
                LOG.info("File processed");
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            return avatars;
        } else {
            LOG.error("Wrong file content type");
            return null;
        }
    }

    private void deleteChatImages(List<String> images) {
        LOG.info("Deleting list of {} images", images.size());
        images.forEach(img -> {
            try {
                Files.deleteIfExists(Paths.get(uploadDir + File.separator + img));
                LOG.debug("Image {} deleted", img);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        });
        LOG.info("Images deleted");
    }

    @Override
    public Map<String, Long> getOnlineUsers() {
        LOG.debug("Getting list of online users");
        Map<String, Long> users = new HashMap<>();
        try {
            List<User> userList = chatService.getChatById(1L).getUserList();
            userList.forEach(user -> users.put(user.getNickName(), user.getId()));
            LOG.debug("Returned list of {} users", users.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return users;
    }

    // Метод получает все сообщения в чате позднее даты, заданной полем olderThan. Для приватного чата - все
    // сообщения позднее месяца
    private List<Message> findAllByChat(Chat chat, boolean isPrivate) {
        LocalDateTime date;
        if (isPrivate) {
            date = LocalDateTime.now().minusMonths(1L);
        } else {
            switch (olderThan) {
                case "week":
                    date = LocalDateTime.now().minusWeeks(1L);
                    break;
                case "two-weeks":
                    date = LocalDateTime.now().minusWeeks(2L);
                    break;
                case "month":
                    date = LocalDateTime.now().minusMonths(1L);
                    break;
                default:
                    date = LocalDateTime.now().minusWeeks(1L);
            }
        }
        return messageRepository.findAllByChatAndDate(chat.getId(), date);
    }

    @Scheduled(cron = "0 0 0 * * 0")
    private void cleanMessages() {
        if (!olderThan.equals("never")) {
            LOG.info("Starting scheduled task of deleting messages in group chat");
            deleteMessages(false, false, "-");
        }
    }

    // Метод удаляет сообщеня и связанные с ними изображения. Для приватного чата - если параметр deleteAll = true,
    // то удаляются все сообщения, иначе удаляются только сообщения позднее месяца. Для общего чата - удаляются сообщения
    // позднее даты заданной адмном(поле olderThan)
    public void deleteMessages(boolean isPrivate, boolean deleteAll, String chatToken) {
        List<Message> messages = null;
        if (!isPrivate) {
            LOG.debug("Getting list of group chat messages to delete");
            try {
                messages = findAllByChat(chatService.getChatById(1L), false);
                LOG.debug("Returned list of {} messages", messages.size());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        } else {
            LOG.debug("Getting list of messages to delete");
            Chat chat;
            try {
                chat = chatService.getChatByToken(chatToken);
                messages = deleteAll ? messageRepository.findAllByChat(chat) : findAllByChat(chat, true);
                LOG.debug("Returned list of {} messages", messages.size());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        List<String> images = new ArrayList<>();
        Objects.requireNonNull(messages).forEach(msg -> {
            images.add(msg.getOriginalImg());
            images.add(msg.getThumbnailImg());
        });
        deleteChatImages(images);
        messages.forEach(msg -> removeMessageById(msg.getId()));
        LOG.debug("All messages successfully deleted");
    }
}