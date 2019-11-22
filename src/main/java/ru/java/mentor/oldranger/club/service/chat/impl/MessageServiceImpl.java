package ru.java.mentor.oldranger.club.service.chat.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.ChatRepository.MessageRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.service.chat.MessageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;
    private String uploadDir = "./uploads/chat";

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

    private String thumbnailImage(String imageName, int size, MultipartFile file) throws IOException {
        String resultFileName = UUID.randomUUID().toString() + "__" +
                size + "px" + StringUtils.cleanPath(file.getOriginalFilename());
        String resultFileLocation = Paths
                .get(uploadDir + File.separator + resultFileName).toString();
        Thumbnails.of(uploadDir + File.separator + imageName)
                .size(size, size)
                .toFile(resultFileLocation);
        return resultFileName;
    }

    @Override
    public Map<String,String> processImage(MultipartFile file) throws IOException {
        Map<String,String> avatars = new HashMap<>();
        avatars.put("originalImg", uploadImage(file));
        avatars.put("thumbnailImg", thumbnailImage(avatars.get("originalImg"),150,file));
        return avatars;
    }


}