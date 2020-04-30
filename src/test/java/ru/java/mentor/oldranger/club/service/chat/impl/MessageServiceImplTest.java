package ru.java.mentor.oldranger.club.service.chat.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import ru.java.mentor.oldranger.club.dao.ChatRepository.MessageRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.media.FileInChatService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    @Mock
    private PhotoService photoService = Mockito.mock(PhotoService.class);
    @Mock
    private ChatService chatService = Mockito.mock(ChatService.class);
    @Mock
    private Chat chat = Mockito.mock(Chat.class);
    @Mock
    private FileInChatService fileInChatService = Mockito.mock(FileInChatService.class);
    @Mock
    private UserService userService = Mockito.mock(UserService.class);

    private MessageServiceImpl messageService = new MessageServiceImpl(messageRepository, chatService, photoService,fileInChatService, userService);

    private static List<Message> messages = new ArrayList<>(Arrays.asList(
            new Message(1L, null, "testImg1", null, null, null, null, null, null, null, null, null),
            new Message(2L, null, null, null, null,  null, null, null, null, null, null, null)));

    @Test
    public void findFirstMessageByChat() {
        messageService.findFirstMessageByChat(chat);
        Mockito.verify(messageRepository, Mockito.times(1))
                .findFirstByChatOrderByMessageDateAsc(chat);
    }

    @Test
    public void processImage() throws IOException {
        FileInputStream inputStream = new FileInputStream(
                new File("src/test/resource/media/testImg.jpg"));
        MockMultipartFile mockFile = new MockMultipartFile(
                "testName", "testFileName", "image/jpeg", inputStream);

        Map<String, String> map = messageService.processImage(mockFile);

        Assert.assertTrue(map.get("originalImg").contains("testFileName"));
        Assert.assertTrue(map.get("thumbnailImg").contains("testFileName"));
    }

    @Test
    public void processImageFail() throws IOException {
        FileInputStream inputStream = new FileInputStream(
                new File("src/test/resource/media/testImg.jpg"));
        MockMultipartFile mockFile = new MockMultipartFile(
                "testName", "testFileName", "someType", inputStream);

        Map<String, String> map = messageService.processImage(mockFile);

        Assert.assertNull(map);
    }

    @Test
    public void getOnlineUsers() {
        List<User> users = new ArrayList<>(Arrays.asList(
                new User(1L, null, null, null, "user1", null, null, null, null, null, null),
                new User(2L, null, null, null, "user2", null, null, null, null, null, null)));
        chat.setUserList(users);
        Mockito.when(chatService.getChatById(Mockito.anyLong())).thenReturn(chat);
        Mockito.when(chat.getUserList()).thenReturn(users);

        Map<String, Long> usersMap = messageService.getOnlineUsers();

        Assert.assertEquals(usersMap.size(), users.size());
        users.forEach(u -> Assert.assertTrue(usersMap.containsKey(u.getNickName())));
    }

    @Test
    public void deleteMessagesInPublicChat() {
        chat.setPrivacy(false);
        List<Message> messages = new ArrayList<>(Arrays.asList(
                new Message(1L, null, "testImg1", null, null, null, null,null, null, null, null, null),
                new Message(2L, null, null, null, null, null, null, null,null, null, null, null)));
        Mockito.when(chatService.getGroupChat()).thenReturn(chat);
        Mockito.when(messageRepository.findAllByChat(chat)).thenReturn(messages);

        messageService.deleteMessages(false, false, "token");

        verifyDeleteMessages();
    }

    @Test
    public void deleteMessagesInPrivateChatDeleteAllFalse() {
        String chatToken = "token";
        chat.setPrivacy(true);
        chat.setToken(chatToken);
        chat.setId(1L);
        Mockito.when(chatService.getChatByToken(chatToken)).thenReturn(chat);
        Mockito.when(messageRepository.findAllByChatAndDate(Mockito.eq(chat.getId()),
                Mockito.any(LocalDateTime.class))).thenReturn(messages);

        messageService.deleteMessages(true, false, chatToken);

        verifyDeleteMessages();
    }

    private void verifyDeleteMessages() {
        List<String> images = messages.stream().map(Message::getOriginalImg).collect(Collectors.toList());
        images.forEach(e -> Mockito.verify(photoService, Mockito.times(1)).deletePhotoByName(e));
        messages.forEach(e -> Mockito.verify(messageRepository, Mockito.times(1)).deleteById(e.getId()));
    }

    @Test
    public void deleteMessage() {
        long id = 1;
        Message message = new Message();
        message.setOriginalImg("testImage");
        Mockito.doReturn(Optional.of(message)).when(messageRepository).findById(id);

        messageService.deleteMessage(id);

        Mockito.verify(photoService, Mockito.times(1)).deletePhotoByName(message.getOriginalImg());
        Mockito.verify(messageRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void findMessageFail() {
        assertThrows(RuntimeException.class,
                () -> messageService.findMessage(null),
                "Did not find message by id -Null");
    }
}