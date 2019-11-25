package ru.java.mentor.oldranger.club.service.chat.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private ChatService chatService;
    private String uploadDir = "./uploads/chat";

    public MessageServiceImpl(MessageRepository messageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
    }

    @Override
    public void addMessage(Message message) {
        messageRepository.save(message);
    }

    @Override
    public void removeMessageById(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public Page<Message> getPagebleMessages(Chat chat, Pageable pageable) {
        return messageRepository.findAllByChat(chat,pageable);
    }

    private String uploadImage(MultipartFile file) throws IOException {
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdir();
        }
        String resultFileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
        Path copyLocation = Paths
                .get(uploadDir + File.separator + resultFileName);
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        return resultFileName;
    }

    private String thumbnailImage(String imageName, MultipartFile file) throws IOException {
        String resultFileName = UUID.randomUUID().toString() + "__" +
                150 + "px" + StringUtils.cleanPath(file.getOriginalFilename());
        String resultFileLocation = Paths
                .get(uploadDir + File.separator + resultFileName).toString();
        Thumbnails.of(uploadDir + File.separator + imageName)
                .size(150, 150)
                .toFile(resultFileLocation);
        return resultFileName;
    }

    @Override
    public Map<String,String> processImage(MultipartFile file) throws IOException {
        if (("image/jpeg").equals(file.getContentType()) || ("image/png").equals(file.getContentType())){
            Map<String,String> avatars = new HashMap<>();
            avatars.put("originalImg", uploadImage(file));
            avatars.put("thumbnailImg", thumbnailImage(avatars.get("originalImg"),file));
            return avatars;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Long> getOnlineUsers() {
        List<User> userList = chatService.getChatById(1L).getUserList();
        Map<String, Long> users = new HashMap<>();
        for (User user : userList) {
            users.put(user.getNickName(), user.getId());
        }
        return users;
    }

}