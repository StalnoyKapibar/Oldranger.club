package ru.java.mentor.oldranger.club.service.user.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.java.mentor.oldranger.club.dao.UserRepository.InviteRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvitationServiceImplTest {

    @Autowired
    private InvitationService invitationService;

    @MockBean
    private InviteRepository inviteRepository;

    @Test
    public void getCurrentKeyTest() {
        User user = new User();
        user.setId(1L);
        List<InvitationToken> tokenList = new ArrayList<>();
        tokenList.add(new InvitationToken(invitationService.generateKey(), user));
        Mockito.when(inviteRepository.findAllByUserAndUsedAndMail(ArgumentMatchers.eq(user), ArgumentMatchers.anyBoolean(), ArgumentMatchers.isNull())).thenAnswer(a -> tokenList);
        Assert.assertEquals(tokenList.get(0).getKey(), invitationService.getCurrentKey(user));
    }

    @Test
    public void checkShelfLifeTest() {
        User user = new User();
        user.setId(1L);
        InvitationToken invitationToken = new InvitationToken(invitationService.generateKey(), user);
        Mockito.when(inviteRepository.findByKey(ArgumentMatchers.eq(invitationToken.getKey()))).thenAnswer(a -> invitationToken);
        Assert.assertEquals(false, invitationService.checkShelfLife(invitationToken));
    }

    @Test
    public void markInviteOnMailAsUsedTest() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail");
        List<InvitationToken> tokenList = new ArrayList<>();
        tokenList.add(new InvitationToken(invitationService.generateKey(), user));
        Mockito.when(inviteRepository.findAllByMailAndUsed(ArgumentMatchers.eq(user.getEmail()), ArgumentMatchers.anyBoolean())).thenAnswer(a -> tokenList);
        invitationService.markInviteOnMailAsUsed(user.getEmail());
        Assert.assertEquals(tokenList.get(0).getUsed(), true);
    }

}
