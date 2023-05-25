package user.study.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.dto.request.UserRequestDto;
import user.study.member.domain.dto.response.UserResponseDto;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;
import user.study.member.service.UserJwtService;
import user.study.member.service.UserService;

@RestController
@RequestMapping("/jwt-login")
@RequiredArgsConstructor
public class JwtLoginController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserJpaRepository userRepository;
    private final UserJwtService userJwtService;

    // 모든 사람이 접근 가능
    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

//    로그인, JWT 생성
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto.LoginDTO loginDTO){
        UserResponseDto.TokenInfo tokenInfo = userJwtService.login(loginDTO.getName(), loginDTO.getPwd());
        if(tokenInfo != null){
            return new ResponseEntity<>(tokenInfo, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("로그인 처리 오류",HttpStatus.BAD_REQUEST);
        }
    }

//    회원 가입
    @PostMapping("join")
    public String join(@RequestBody User user){
        System.out.println("Call Post Join");
        user.setPwd(bCryptPasswordEncoder.encode(user.getPwd()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return "회원가입 완료";
    }

    // Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가능.
    // 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.
    @GetMapping("user")
    public User userPage(Authentication authentication){
        PrincipalDetails2 principalDetails2 = (PrincipalDetails2) authentication.getPrincipal();
        User user = principalDetails2.getUser();
        System.out.println(user.toString());
        return user;
    }

    @GetMapping("admin")
    public String adminPage() {
        return "Admin Page";
    }
}
