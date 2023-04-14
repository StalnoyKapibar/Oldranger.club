package ru.oldranger.club.service.forum.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.oldranger.club.dao.ForumRepository.ImageCommentRepository;
import ru.oldranger.club.model.forum.ImageComment;
import ru.oldranger.club.service.forum.ImageCommnetService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageCommentServiceImpl implements ImageCommnetService {

    @NonNull
    private ImageCommentRepository imageCommentRepository;

    private String uploadDir = "./uploads/imageComment";

    public void save(ImageComment imageComment) {
        log.info("Saving imageComment {}", imageComment);
        try {
            imageCommentRepository.save(imageComment);
            log.info("Avatar imageComment");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<ImageComment> findAllByCommentId(Long commentId) {
        return imageCommentRepository.findAllByCommentId(commentId);
    }

    public String uploadImage(MultipartFile file) throws IOException {
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

    public ImageComment createNewImage(MultipartFile file)  {
        log.debug("Creating new image comment");
        ImageComment imageComment = new ImageComment();
        try {
            imageComment.setImg(uploadImage(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageComment;
    }

}
