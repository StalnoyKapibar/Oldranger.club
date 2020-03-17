package ru.java.mentor.oldranger.club.service.media.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.MediaRepository.FileInChatRepository;
import ru.java.mentor.oldranger.club.model.media.FileInChat;
import ru.java.mentor.oldranger.club.service.media.FileInChatService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileInChatServiceImpl implements FileInChatService {
    private FileInChatRepository fileInChatRepository;

    @Value("${filesInChat.location}")
    private String filesDir;

    @Autowired
    public FileInChatServiceImpl(FileInChatRepository fileInChatRepository) {
        this.fileInChatRepository = fileInChatRepository;
    }

    @Override
    public FileInChat save(MultipartFile file, Long chatId) {
        FileInChat savedFile = new FileInChat();
        try {
            String fileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
            String pathToFile = "chat_" + chatId + File.separator;
            String resultFileName = pathToFile + fileName;
            File uploadPath = new File(filesDir + File.separator + "chat_" + chatId);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path copyLocation = Paths.get(filesDir + File.separator + resultFileName);
            Files.copy(file.getInputStream(), copyLocation);
            log.info("file saved");
            savedFile.setFileName(fileName);
            savedFile.setFilePath(resultFileName);
            savedFile.setChatID(chatId);
            fileInChatRepository.save(savedFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.println("((((");
        }
        return savedFile;
    }

    @Override
    public List<FileInChat> findAllByChat(Long chatId) {
        return fileInChatRepository.findByChatID(chatId);
    }

    @Override
    public FileInChat findByName(String fileName) {
        return fileInChatRepository.findByFileName(fileName);
    }

    @Override
    public void deleteAllByChat(Long chatId) {
        log.info("Deleting files with chatId = {}", chatId);
        try {
            List<FileInChat> deleteFiles = fileInChatRepository.findByChatID(chatId);

            for (FileInChat delFile : deleteFiles) {
                File file = new File(filesDir + File.separator + delFile.getFilePath());
                FileSystemUtils.deleteRecursively(file);
            }
            fileInChatRepository.deleteAllByChatID(chatId);
            log.debug("All files in chat {} deleted", chatId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
