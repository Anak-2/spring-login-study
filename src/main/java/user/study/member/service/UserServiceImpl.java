package user.study.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service(value="userServiceImpl")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public List<User> login(String userName, String userPwd) {
        Optional<List<User>> user =  userRepository.findByName(userName);
        return user.orElse(null); // 있으면 user 객체 없으면 null 반환
    }

    public void join(FormUser formUser){
        User user = formUser.toEntity();
        log.info("join id: {} pwd: {}",user.getName(),user.getPwd());
        userRepository.save(user);
    }

    public boolean checkNameDuplicate(String name){
        if(userRepository.findByName(name).isPresent()){
            return !userRepository.findByName(name).get().isEmpty();
        }
        return false;
    }

    public User getLoginUser(String userName){
        Optional<List<User>> userList = userRepository.findByName(userName);
        if(userList.get().isEmpty()) return null;
        else return userList.get().get(0);
    }

    
//    JPA REPOSITORY 를 사용한 메소드 --> 일반 JPA의 return 값 잘못 설계함 ㅠ 수정 필요
    @Override
    public User login(User user) {
        return null;
    }

    @Override
    public boolean join(User user) {
        return false;
    }
}
