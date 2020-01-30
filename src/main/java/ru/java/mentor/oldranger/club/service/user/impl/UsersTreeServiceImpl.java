package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.UsersTreeRepository;
import ru.java.mentor.oldranger.club.dto.UsersTreeDto;
import ru.java.mentor.oldranger.club.service.user.UsersTreeService;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UsersTreeServiceImpl implements UsersTreeService {

    private UsersTreeRepository usersTreeRepository;
    private UserRepository userRepository;

    @Override
    public List<UsersTreeDto> getInvitedUsersTreeById(long userId) {
        List<Tuple> listUserDto = usersTreeRepository.getInvitedUsersTreeById(userId);
        List<UsersTreeDto> listDto = new ArrayList<>();
//        for (Tuple t: listUserDto) {
//            listDto.add(new UsersTreeDto(
//                    t.get("parent") == null ? null : Long.parseLong(t.get("parent").toString()),
//                    t.get("nick_name").toString(),
//                    Long.parseLong(t.get("NEW_USER").toString())
//                    ));
//        }
        return usersTreeRepository.getInvitedUsersTreeById(userId).stream().map(e -> new UsersTreeDto(
                e.get("parent") == null ? null : Long.parseLong(e.get("parent").toString()),
                e.get("nick_name").toString(),
                Long.parseLong(e.get("NEW_USER").toString())
        )).collect(Collectors.toList());
    }
}
