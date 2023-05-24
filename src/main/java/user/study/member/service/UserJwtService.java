package user.study.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.dto.response.UserResponseDto;
import user.study.member.domain.user.User;
import user.study.member.filter.jwtV2.JwtTokenProvider;
import user.study.member.repository.UserJpaRepository;

import java.util.List;

@Service("userJwtServiceImpl")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserJwtService{

    private final UserJpaRepository userJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserResponseDto.TokenInfo login(String userName, String userPwd) {
        try{
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(userName,userPwd);
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            return JwtTokenProvider.generateToken(authentication);
        }catch(Exception e){
            e.printStackTrace();
//            ToDo: ExceptionHandler 를 사용하거나 throw new CustomException() 로 처리해주기
            log.error("로그인 실패");
        }
        return null;
    }

}
