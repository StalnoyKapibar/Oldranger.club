package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.List;

@SpringBootTest
public class UserCacheTest {
    @Autowired
    private UserService userService;

    @Test
    public void getAllUserCacheTest() {
        //при дебаге тут заходит в метод
        List<User> list = userService.findAll();
        for (User user :
                list) {
            System.out.println(user.getId());
        }
        //тут не заходит
        List<User> list2 = userService.findAll();
        for (User user :
                list2) {
            System.out.println(user.getId());
        }
        //тут удаляем юзера чем отчистим кэш
        userService.deleteById(5L);
        //и снова наполняем кэш заходом в метод
        List<User> list3 = userService.findAll();
        for (User user :
                list3) {
            System.out.println(user.getId());
        }

    }
}
