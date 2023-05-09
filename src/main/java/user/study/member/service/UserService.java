package user.study.member.service;

import org.springframework.stereotype.Service;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;

import java.util.List;
import java.util.Optional;
// 과제: UserService 인터페이스를 좀 더 객체지향 적으로 설계하기
public interface UserService {

//    일반 JPA 를 이용한 메소드
    List<User> login(String userName, String userPwd);
    void join(FormUser formUser);
    boolean checkNameDuplicate(String name);
    User getLoginUser(String userName);

//    JPA Repository 를 이용한 메소드
    User login(User user);
    boolean join(User user);
}
