package ru.oldranger.club.service.utils.impl;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.oldranger.club.dao.BlackListRepository;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BlackList;
import ru.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
class BlackListServiceImplTest {
    private BlackListServiceImpl blackListService;

    @Mock
    private BlackListRepository blackListRepository = Mockito.mock(BlackListRepository.class);

    @Mock
    private UserService userService = Mockito.mock(UserService.class);
    ;

    @BeforeEach
    void initSomeCase() {
        blackListService = new BlackListServiceImpl(blackListRepository, userService);
    }

    @Test
    public void userSearchBlackListByUserIdOneClearTest() {
        List<BlackList> blackLists = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        blackLists.add(new BlackList(1L, user, LocalDateTime.now().minusMinutes(1)));
        Mockito.when(blackListRepository.findByUserId(ArgumentMatchers.anyLong())).thenAnswer(a -> blackLists);
        boolean res = blackListService.userSearchBlackListByUserId(user.getId());
        Mockito.verify(blackListRepository, Mockito.times(1)).delete(blackLists.get(0));
        Assert.assertEquals(res, true);
    }

    @Test
    public void userSearchBlackListByUserIdTwoNoClearTest() {
        List<BlackList> blackLists = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        blackLists.add(new BlackList(1L, user, LocalDateTime.now().minusMinutes(1)));
        blackLists.add(new BlackList(2L, user, LocalDateTime.now().plusDays(1)));
        Mockito.when(blackListRepository.findByUserId(ArgumentMatchers.anyLong())).thenAnswer(a -> blackLists);
        boolean res = blackListService.userSearchBlackListByUserId(user.getId());
        Mockito.verify(blackListRepository, Mockito.times(1)).delete(blackLists.get(0));
        Mockito.verify(blackListRepository, Mockito.times(0)).delete(blackLists.get(1));
        Assert.assertEquals(res, true);
    }
}
