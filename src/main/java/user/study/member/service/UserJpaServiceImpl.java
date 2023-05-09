package user.study.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;

@Service("userJpaServiceImpl")
@RequiredArgsConstructor
public class UserJpaServiceImpl implements UserService{

    private final UserJpaRepository userJpaRepository;

    public User login(User user){
        Optional<User> findUser = userJpaRepository.findByName(user.getName());
        return findUser.orElse(null);
    }

    public boolean join(User user){
        if(userJpaRepository.existsByName(user.getName())){
            userJpaRepository.save(user);
            return true;
        }
        return false;
    }

    //  일반 JPA 를 사용한 메소드 --> JPA Repository 의 return 값 잘못 설계함 ㅠ 수정 필요
    @Override
    public List<User> login(String userName, String userPwd) {
        return null;
    }

    @Override
    public void join(FormUser formUser) {

    }

    @Override
    public boolean checkNameDuplicate(String name) {
        return false;
    }

    @Override
    public User getLoginUser(String userName) {
        return null;
    }
}
