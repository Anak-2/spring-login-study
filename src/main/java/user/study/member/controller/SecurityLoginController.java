package user.study.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import user.study.member.config.auth.PrincipalDetails;
import user.study.member.config.auth.PrincipalDetailsService;
import user.study.member.config.oauth.MyOAuth2UserService;
import user.study.member.config.oauth.PrincipalDetails2;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;
import user.study.member.service.UserService;
import user.study.member.service.UserServiceImpl;

import java.text.Normalizer;

// Spring Security 를 이용한 로그인
// 과제 : security login 을 이용해서 home.html 띄울 때 시큐리티의 session 이용해서 user 정보 꺼내오는 방법 생각하기
@Controller
@RequestMapping("/security-login")
@Slf4j
public class SecurityLoginController {

    private final UserService userService;
    private final MyOAuth2UserService myOAuth2UserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityLoginController(
            @Qualifier("userJpaServiceImpl") // Lombok 이 Qualifier 는 자동 생성 안해주므로 직접 생성자 생성
            UserService userService,
            MyOAuth2UserService myOAuth2UserService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.myOAuth2UserService = myOAuth2UserService;
    }

    //    Main 화면
//    과제: Spring Security의 Session 이용해서 User 존재하면 정보 띄워주기
//    해결: @AuthenticationPrincipal 이용해서 Authentication 객체에서 최근 로그인한 UserDetails 객체 받아오기
//    과제: PrincipalDetails 를 우리가 필요한 추가 정보 (ex. 집주소, 전화번호) 같은 것을 추가해서 User Entity로 가져오기
    @GetMapping(value = {"", "/"})
    public String home(@AuthenticationPrincipal PrincipalDetails2 principalDetails2, Model model) {
        if(principalDetails2 != null){
            model.addAttribute("user", principalDetails2.toEntity());
        }
        return "home";
    }

//    접속한 User의 정보를 조회하는 페이지
//    Spring Security로 로그인한 User의 정보는 security를 통한 일반 로그인(UserDetails 객체)이나
//    OAuth 를 통한 외부 로그인(OAuth2User)인데
//    PrincipalDetails2 DTO 를 이용해 @AuthenticationPrincipal UserDetails 와 OAuth2User 둘 다 받을 수 있도록 객체를 만들었다
//    과제: DB에 User Entity 로 넣었기 때문에 User Entity 로 반환할 수 있다. PrincipalDetails2 에는 많은 필드가 있어서 JSON 으로 반환하면 너무 많은 정보가 노출
//    과제2: 한 Controller 에 2개 Service (MyOAuth2UserService, UserJpaServiceImpl)
    @GetMapping(value = "/user")
    public @ResponseBody User userInfo(@AuthenticationPrincipal PrincipalDetails2 principalDetails2){
//        ToDo: principalDetails2 로 받아서 id가 null임 수정 필요
        return principalDetails2.toEntity();
    }

    //    Login 처리
//    과제: 기존 login.html 쓸 수 있도록 설정하기 (이전 securityLogin 사용)
//    해결! securityLogin.html 삭제해도 됨
    @GetMapping(value = "/login")
    public String loginPage(Model model) {
        model.addAttribute("formUser", new FormUser());
        return "login";
    }

//    *** login process 는 security에게 맡기기 때문에 필요x
//    @PostMapping(value="/login")

    //    Join 처리
    @GetMapping(value = "/create")
    public String createForm(Model model) {
        model.addAttribute("formUser", new FormUser());
        return "createForm";
    }

    //    ToDo: DB 에 name 중복 확인을 2번 하는 낭비 해결 필요
    @PostMapping(value = "/create")
    public String createUser(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult) {
        if (userService.checkNameDuplicate(formUser.getName())) {
            bindingResult.addError(new FieldError("formUser", "name", "ID 중복"));
            return "createForm";
        } else {
            User user = formUser.toEntity();
            userService.join(user);
            return "redirect:/security-login";
        }
    }

    //    Admin 페이지 처리 (security 에게 의존)
    @GetMapping(value = "/admin")
    public String getAdminPage() {
        return "admin";
    }

//      과제: logout 만들기 (spring security 에서 제공하는거 써도 좋고)
}
