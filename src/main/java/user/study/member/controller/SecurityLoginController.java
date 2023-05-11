package user.study.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import user.study.member.config.auth.PrincipalDetails;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityLoginController(
            @Qualifier("userJpaServiceImpl") // Lombok 이 Qualifier 는 자동 생성 안해주므로 직접 생성자 생성
            UserService userService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //    Main 화면
//    과제: Spring Security의 Session 이용해서 User 존재하면 정보 띄워주기
//    해결: @AuthenticationPrincipal 이용해서 Authentication 객체에서 최근 로그인한 UserDetails 객체 받아오기
//    과제: PrincipalDetails 를 우리가 필요한 추가 정보 (ex. 집주소, 전화번호) 같은 것을 추가해서 User Entity로 가져오기
    @GetMapping(value = {"", "/"})
    public String home(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        if(principalDetails != null){
            User user = User.formBuilder()
                    .name(principalDetails.getUsername())
                    .pwd(principalDetails.getPassword())
                    .role(Role.GUEST) // 과제: Role 클래스도 principalDetails 에서 받아오도록 바꾸기
                    .build();
            model.addAttribute("user", user);
        }
        return "home";
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

    //    과제: DB 에 name 중복 확인을 2번 하는 낭비 해결 필요
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
}
