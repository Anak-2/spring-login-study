package user.study.member.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;

import java.util.Optional;

// Security 설정(Config) 에서 loginProcessingUrl("/login")
// 설정을 해놨기 때문에, login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어있는
// loadUserByUsername 함수가 실행
@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

//    Authentication Manager 의 authenticate 실행될 때 호출
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername 호출");
        Optional<User> findUser = userJpaRepository.findByName(username);
        User user = findUser.orElse(null);
        log.info("user : {}", user);
        if(user != null){
            PrincipalDetails2 principalDetails2 = new PrincipalDetails2(user);
            return principalDetails2;
        }
        throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다");
    }
}
