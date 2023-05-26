package user.study.member.controller;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.dto.request.UserRequestDto;
import user.study.member.domain.dto.response.UserResponseDto;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;
import user.study.member.service.UserJwtService;
import user.study.member.service.UserService;

import java.io.IOException;

// ToDo: JWT 를 이용한 Restful API 를 사용할 때 OAuth2 로그인 어떻게 처리할 지 생각하기 (OAuth2 로그인 성공 시 jwt 토큰을 어디서 발급해서 어떻게 줄까?)
@RestController
@RequestMapping("/jwt-login")
@RequiredArgsConstructor
public class JwtLoginController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserJpaRepository userRepository;
    private final UserJwtService userJwtService;

    // 모든 사람이 접근 가능
    @GetMapping(value = "/home")
    public ModelAndView home(HttpServletResponse response) throws IOException{
        ModelAndView mv = new ModelAndView("home");
        return mv;
    }

//    로그인 페이지에서 로그인 처리하는 시도 -> application/x-www-form-urlencoded is not supported 오류 발생
//    ToDo: @RestController 여서 모든 요청, 응답이 json 으로 받도록 되어있는 건가?
//          @GetMapping(login) 시 @PostMapping(login) 작동 안하는 이유 찾기
//    @GetMapping(value ="/login")
//    public ModelAndView loginPage(HttpServletResponse response) throws IOException {
////        String redirect_uri = "http://localhost:8080/jwt-login/login";
////        response.sendRedirect(redirect_uri);
//        ModelAndView mv = new ModelAndView("login");
//        mv.addObject("formUser",new FormUser());
//        return mv;
//    }

//    로그인, JWT 생성
    @PostMapping(value ="/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto.LoginDTO loginDTO){
        UserResponseDto.TokenInfo tokenInfo = userJwtService.login(loginDTO.getName(), loginDTO.getPwd());
        if(tokenInfo != null){
            return new ResponseEntity<>(tokenInfo, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("로그인 처리 오류",HttpStatus.BAD_REQUEST);
        }
    }

//    회원 가입
    @PostMapping(value ="/join")
    public String join(@RequestBody User user){
        System.out.println("Call Post Join");
        user.setPwd(bCryptPasswordEncoder.encode(user.getPwd()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return "회원가입 완료";
    }

    // Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가능.
    // 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.
    @GetMapping(value ="/user")
    public User userPage(Authentication authentication){
        PrincipalDetails2 principalDetails2 = (PrincipalDetails2) authentication.getPrincipal();
        User user = principalDetails2.getUser();
        System.out.println(user.toString());
        return user;
    }

    @GetMapping(value ="/admin")
    public String adminPage() {
        return "Admin Page";
    }
}
