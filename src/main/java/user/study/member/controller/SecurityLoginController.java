package user.study.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.service.UserService;
import user.study.member.service.UserServiceImpl;

// Spring Security 를 이용한 로그인
@Controller
@RequestMapping("/security-login")
@Slf4j
public class SecurityLoginController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityLoginController(
        @Qualifier("userJpaServiceImpl") // Lombok 이 Qualifier 는 자동 생성 안해주므로 직접 생성자 생성
        UserService userService,
        BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping(value={"", "/"})
    public String home(){
        return "home";
    }

    @GetMapping(value="/login")
    public String loginPage(Model model){
        model.addAttribute("formUser", new FormUser());
        return "login";
    }

    @PostMapping(value="/login")
    public String login(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult){
        User loginUser = userService.login(formUser.toEntity());
        if(loginUser != null){
            return "home";
        }else{
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다");
//            과제 : security login 을 이용해서 home.html 띄울 때 시큐리티의 session 이용해서 user 정보 꺼내오는 방법 생각하기
            return "redirect:/security-login";
        }
    }
}
