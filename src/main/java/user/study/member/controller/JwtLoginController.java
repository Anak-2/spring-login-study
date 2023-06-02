package user.study.member.controller;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.dto.request.UserRequestDto;
import user.study.member.domain.dto.response.UserResponseDto;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;
import user.study.member.filter.jwtV2.JwtTokenProvider;
import user.study.member.global.exception.NotAuthorizedException;
import user.study.member.repository.UserJpaRepository;
import user.study.member.service.UserJwtService;
import user.study.member.service.UserService;
import user.study.member.service.token.TokenService;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

// ToDo: JWT 를 이용한 Restful API 를 사용할 때 OAuth2 로그인 어떻게 처리할 지 생각하기 (OAuth2 로그인 성공 시 jwt 토큰을 어디서 발급해서 어떻게 줄까?)
@RestController
@RequestMapping("/jwt-login")
@RequiredArgsConstructor
@Slf4j
public class JwtLoginController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserJpaRepository userRepository;
    private final UserJwtService userJwtService;
    private final TokenService tokenService;

    // 모든 사람이 접근 가능
    @GetMapping(value = "/home")
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String accessToken = request.getParameter("accessToken");
        ModelAndView mv = new ModelAndView("home");
        if(accessToken != null) {
            System.out.println("accessToken: " + accessToken);
        }
        return mv;
    }

//    로그인 페이지에서 로그인 처리하는 시도 -> application/x-www-form-urlencoded is not supported 오류 발생
//    ToDo: @RestController 여서 모든 요청, 응답이 json 으로 받도록 되어있는 건가?
//          @GetMapping(login) 시 @PostMapping(login) 작동 안하는 이유 찾기
    @GetMapping(value ="/login")
    public ModelAndView loginPage(HttpServletResponse response) throws IOException {
        System.out.println("Call Get Login");
        ModelAndView mv = new ModelAndView("jwtLogin");
        return mv;
    }

//    로그인, JWT 생성
    @PostMapping(value ="/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto.LoginDTO loginDTO, HttpServletResponse response) throws ServletException,IOException{
        UserResponseDto.TokenInfo tokenInfo = userJwtService.login(loginDTO.getName(), loginDTO.getPwd());
        if(tokenInfo != null){
            Cookie cookie = new Cookie("refreshToken", URLEncoder.encode(tokenInfo.getRefreshToken(), "UTF-8"));
            cookie.setMaxAge((int)JwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            tokenService.addToken(tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime());
            return new ResponseEntity<>(tokenInfo, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("로그인 처리 오류",HttpStatus.BAD_REQUEST);
        }
    }

//    로그아웃, JWT 삭제
    @DeleteMapping(value="/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        String cookieName = "refreshToken";
        String rJwt = null;
        for(Cookie c : cookies){
            if(cookieName.equals(c.getName())){
                rJwt = c.getValue();
            }
        }
        tokenService.removeToken(rJwt);
        Cookie cookie = new Cookie("refreshToken",null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

//    회원 가입
    @PostMapping(value ="/join")
    public String join(@RequestBody User user){
        System.out.println("Call Post Join");
        if (userRepository.findByName(user.getName()).isEmpty()){
            user.setPwd(bCryptPasswordEncoder.encode(user.getPwd()));
            user.setRole(Role.USER);
            userRepository.save(user);
            return "회원가입 완료";
        }
        else{
            throw new NotAuthorizedException("이미 존재하는 이름입니다");
        }
    }

    // Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가능.
    // 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.
    @GetMapping(value ="/user")
    public ResponseEntity<?> userPage(HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
        System.out.println("/user Call!");
//        Filter 에서 생성한 response 의 헤더에 Authorization 이 있으면 accessToken 새로 생긴 것
//        Filter 에서 이용한 Response 가 클라이언트한테 까지 전달되는 것이었다!
//        그래서 밑에 HttpHeaders 에 또 추가할 필요 X
        String accessToken = response.getHeader("Authorization");
        PrincipalDetails2 principalDetails2 = (PrincipalDetails2) authentication.getPrincipal();
        User user = principalDetails2.getUser();
        System.out.println(user.toString());
        if(accessToken != null){
            log.debug("Refresh Access Token");
            HttpHeaders headers = new HttpHeaders();
//            Filter 에서 받은 Response 에 addHeader 하면 클라이언트 한테까지 전달됨!
//            headers.add("Authorization",accessToken);
//            return new ResponseEntity<User>(user,headers,HttpStatus.CREATED);
            return new ResponseEntity<User>(user,HttpStatus.CREATED);
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @GetMapping(value ="/admin")
    public String adminPage() {
        return "Admin Page";
    }
}
