package ru.java.mentor.oldranger.club.service.user.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.java.mentor.oldranger.club.dao.BlackListRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.InviteRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.impl.ArticleServiceImpl;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
class InvitationServiceImplTest {
    private InvitationServiceImpl invitationService;

    @Mock
    private InviteRepository inviteRepository = Mockito.mock(InviteRepository.class);

    @BeforeEach
    void initSomeCase() {
        invitationService = new InvitationServiceImpl(inviteRepository);
    }

    @Test
    public void getCurrentKeyTest() {
        User user = new User();
        user.setId(1L);
        List<InvitationToken> tokenList = new ArrayList<>();
        tokenList.add(new InvitationToken(invitationService.generateKey(), user));
        Mockito.when(inviteRepository.findAllByUserAndUsedAndMail(ArgumentMatchers.eq(user), ArgumentMatchers.anyBoolean(), ArgumentMatchers.isNull())).thenAnswer(a->tokenList);
        Assert.assertEquals(tokenList.get(0).getKey(), invitationService.getCurrentKey(user));
    }

    @Test
    public void checkShelfLifeTest() {
        User user = new User();
        user.setId(1L);
        InvitationToken invitationToken = new InvitationToken(invitationService.generateKey(), user);
        Mockito.when(inviteRepository.findByKey(ArgumentMatchers.eq(invitationToken.getKey()))).thenAnswer(a->invitationToken);
        Assert.assertEquals(false, invitationService.checkShelfLife(invitationToken));
    }

    @Test
    public void markInviteOnMailAsUsedTest() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail");
        List<InvitationToken> tokenList = new ArrayList<>();
        tokenList.add(new InvitationToken(invitationService.generateKey(), user));
        Mockito.when(inviteRepository.findAllByMailAndUsed(ArgumentMatchers.eq(user.getEmail()) , ArgumentMatchers.anyBoolean())).thenAnswer(a->tokenList);
        invitationService.markInviteOnMailAsUsed(user.getEmail());
        Assert.assertEquals(tokenList.get(0).getUsed(), true);
    }

}
