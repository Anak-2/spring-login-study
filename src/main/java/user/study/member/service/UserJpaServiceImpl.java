package user.study.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;

@Service("userJpaServiceImpl")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserJpaServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User login(User user) {
        Optional<User> findUser = userJpaRepository.findByName(user.getName());
        return findUser.orElse(null);
    }

    public boolean join(User user) {
        if (!userJpaRepository.existsByName(user.getName())) {
//            Spring Security 는 비밀번호를 암호화 해야지 Spring Security 의 Login 을 이용할 수 있다
            user.setPwd(bCryptPasswordEncoder.encode(user.getPwd()));
            log.info("Call join from userJpaServiceImpl");
            userJpaRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean checkNameDuplicate(String name) {
        Optional<User> findUser = userJpaRepository.findByName(name);
        return findUser.isPresent();
    }

    public User getLoginUser(String userName) {
        Optional<User> findUser = userJpaRepository.findByName(userName);
        return findUser.orElse(null);
    }

    //  과제: 일반 JPA 를 사용한 메소드 --> JPA Repository 의 return 값 잘못 설계함 ㅠ 수정 필요
    @Override
    public List<User> login(String userName, String userPwd) {
        return null;
    }

    @Override
    public void join(FormUser formUser) {

    }
}
