package ru.java.mentor.oldranger.club.service.chat.impl;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.java.mentor.oldranger.club.service.media.FileInChatService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

import javax.persistence.Tuple;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private ChatService chatService;
    private PhotoService photoService;
    private String uploadDir;
    private String olderThan;
    private FileInChatService fileInChatService;

    public MessageServiceImpl(MessageRepository messageRepository, ChatService chatService, PhotoService photoService, FileInChatService fileInChatService) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        this.photoService = photoService;
        this.fileInChatService = fileInChatService;
        uploadDir = "./media";
        olderThan = "week";
    }

    public void setOlderThan(String olderThan) {
        this.olderThan = olderThan;
    }

    @Override
    public Message findFirstMessageByChat(Chat chat) {
        log.debug("Getting oldest message of chat with id = {}", chat.getId());
        Message message = null;
        try {
            message = messageRepository.findFirstByChatOrderByMessageDateAsc(chat);
            log.debug("Returned message with id = {}", message.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return message;
    }

    @Override
    public void addMessage(Message message) {
        log.info("Saving message {}", message);
        try {
            messageRepository.save(message);
            log.info("Message saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void editMessage(Message message) {
        log.info("Editing message {}", message);
        try {
            messageRepository.save(message);
            log.info("Message edited");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void removeMessageById(Long id) {
        log.info("Deleting message with id = {}", id);
        try {
            messageRepository.deleteById(id);
            log.info("Message deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Page<Message> getPagebleMessages(Chat chat, Pageable pageable) {
        log.debug("Getting page {} of messages for chat with id = {}", pageable.getPageNumber(), chat.getId());
        Page<Message> page = null;
        try {
            page = messageRepository.findAllByChat(chat, pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    private String uploadImage(MultipartFile file) throws IOException {
        log.info("Uploading file {}", file.getOriginalFilename());
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
            log.info("File uploaded");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return resultFileName;
    }

    private String thumbnailImage(String imageName, MultipartFile file) throws IOException {
        log.info("Thumbnailing file {}", file.getOriginalFilename());
        String resultFileName = null;
        try {
            resultFileName = UUID.randomUUID().toString() + "__" +
                    150 + "px" + StringUtils.cleanPath(file.getOriginalFilename());
            String resultFileLocation = Paths
                    .get(uploadDir + File.separator + resultFileName).toString();
            Thumbnails.of(uploadDir + File.separator + imageName)
                    .size(150, 150)
                    .toFile(resultFileLocation);
            log.info("Image thumbnailed");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return resultFileName;
    }

    @Override
    public Map<String, String> processImage(MultipartFile file) {
        log.info("Processing file {}", file.getOriginalFilename());
        if (("image/jpeg").equals(file.getContentType()) || ("image/png").equals(file.getContentType())) {
            Map<String, String> avatars = new HashMap<>();
            try {
                avatars.put("originalImg", uploadImage(file));
                avatars.put("thumbnailImg", thumbnailImage(avatars.get("originalImg"), file));
                log.info("File processed");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return avatars;
        } else {
            log.error("Wrong file content type");
            return null;
        }
    }

    private void deleteChatImages(List<String> images) {
        log.info("Deleting list of {} images", images.size());
        images.forEach(img -> {
            photoService.deletePhotoByName(img);
            log.debug("Image {} deleted", img);
        });
        log.info("Images deleted");
    }

    @Override
    public Map<String, Long> getOnlineUsers() {
        log.debug("Getting list of online users");
        Map<String, Long> users = new HashMap<>();
        try {
            List<User> userList = chatService.getChatById(1L).getUserList();
            userList.forEach(user -> users.put(user.getNickName(), user.getId()));
            log.debug("Returned list of {} users", users.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
            log.info("Starting scheduled task of deleting messages in group chat");
            deleteMessages(false, false, "-");
        }
    }

    // Метод удаляет сообщеня и связанные с ними изображения. Для приватного чата - если параметр deleteAll = true,
    // то удаляются все сообщения, иначе удаляются только сообщения позднее месяца. Для общего чата - удаляются сообщения
    // позднее даты заданной адмном(поле olderThan)
    public void deleteMessages(boolean isPrivate, boolean deleteAll, String chatToken) {
        List<Message> messages = null;
        if (!isPrivate) {
            log.debug("Getting list of group chat messages to delete");
            try {
                Chat chat = chatService.getGroupChat();
                messages = messageRepository.findAllByChat(chat);
                log.debug("Returned list of {} messages", messages.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.debug("Getting list of messages to delete");
            Chat chat;
            try {
                chat = chatService.getChatByToken(chatToken);
                messages = deleteAll ? messageRepository.findAllByChat(chat) : findAllByChat(chat, true);
                log.debug("Returned list of {} messages", messages.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        List<String> images = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        Objects.requireNonNull(messages).forEach(msg -> {
            if (msg.getOriginalImg() != null) {
                images.add(msg.getOriginalImg());
            }
            if (msg.getFileName() != null) {
                fileNames.add(msg.getFileName());
            }
        });
        if (!images.isEmpty()) {
            deleteChatImages(images);
        }
        if (!fileNames.isEmpty()) {
            messages.forEach(msg -> fileInChatService.deleteByFileName(msg.getFileName()));
        }

        messages.forEach(msg -> removeMessageById(msg.getId()));
        log.debug("All messages successfully deleted");
    }

    @Override
    public void deleteMessage(Long id) {
        log.debug("Getting message by id for delete");
        Message message = findMessage(id);
        List<String> images = new ArrayList<>();
        if (message.getOriginalImg() != null) {
            images.add(message.getOriginalImg());
            deleteChatImages(images);
        }
        if (message.getFileName() != null) {
            fileInChatService.deleteByFileName(message.getFileName());
        }
        removeMessageById(id);
    }

    @Override
    public Message findMessage(Long id) {
        log.debug("Getting message by id");
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Did not find message by id - " + id));
    }

    @Override
    public List<Message> findAllByChat(Chat chat) {
        return messageRepository.findAllByChat(chat);
    }

    public Message getLastMessage(Chat chat) {
        log.debug("Get last message by chat");
        return messageRepository.findFirstByChatOrderByMessageDateDesc(chat);
    }

    @Override
    public List<Message> findAllByChatUnread(long id) {
        log.debug("Get all unread message");
        return messageRepository.findAllByChatUnread(id);
    }

    @Override
    public HashMap<Long, Message> getAllChatsLastMessage(List<Chat> chats) {
        HashMap<Long, Message> map = new HashMap<>();
        List<Tuple> tuples = chatService.getChatIdAndLastMessage();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        for (Tuple tuple : tuples) {
            map.put(Long.valueOf(String.valueOf(tuple.get("id_chat"))),
                    (new Message(Long.parseLong(String.valueOf(tuple.get("id_chat"))),
                            String.valueOf(tuple.get("msg_text")),
                            LocalDateTime.parse(String.valueOf(tuple.get("message_date")), format))));
        }
        return map;
    }


    @Override
    public HashMap<Long, Integer> getChatIdAndUnreadMessage() {
        HashMap<Long, Integer> map = new HashMap<>();
        List<Tuple> tuples = messageRepository.getChatIdAndUnreadMessage();
        for (Tuple tuple : tuples) {
            map.put(Long.parseLong(tuple.get("id_chat").toString()), Integer.parseInt(tuple.get("COUNT(is_reading)").toString()));
        }
        return map;
    }
}