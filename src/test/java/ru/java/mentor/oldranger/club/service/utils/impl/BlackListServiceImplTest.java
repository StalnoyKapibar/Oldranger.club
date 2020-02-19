package ru.java.mentor.oldranger.club.service.utils.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.java.mentor.oldranger.club.dao.BlackListRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlackListServiceImplTest {

    @Autowired
    private BlackListService blackListService;

    @MockBean
    private BlackListRepository blackListRepository;


    @Test
    public void userSearchBlackListByUserIdOneClearTest() {
        List<BlackList> blackLists = new ArrayList<>() ;
        User user = new User();
        user.setId(1L);
        blackLists.add(new BlackList(1L, user, LocalDateTime.now().minusMinutes(1)));
        Mockito.when(blackListRepository.findByUserId(ArgumentMatchers.anyLong())).thenAnswer(a->blackLists);
        boolean res =  blackListService.userSearchBlackListByUserId(user.getId());
        Mockito.verify(blackListRepository, Mockito.times(1)).delete(blackLists.get(0));
        Assert.assertEquals(res, true);
    }

    @Test
    public void userSearchBlackListByUserIdTwoNoClearTest() {
        List<BlackList> blackLists = new ArrayList<>() ;
        User user = new User();
        user.setId(1L);
        blackLists.add(new BlackList(1L, user, LocalDateTime.now().minusMinutes(1)));
        blackLists.add(new BlackList(2L, user, LocalDateTime.now().plusDays(1)));
        Mockito.when(blackListRepository.findByUserId(ArgumentMatchers.anyLong())).thenAnswer(a->blackLists);
        boolean res = blackListService.userSearchBlackListByUserId(user.getId());
        Mockito.verify(blackListRepository, Mockito.times(1)).delete(blackLists.get(0));
        Mockito.verify(blackListRepository, Mockito.times(0)).delete(blackLists.get(1));
        Assert.assertEquals(res, true);
    }
}
