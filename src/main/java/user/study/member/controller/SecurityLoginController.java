package user.study.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import user.study.member.domain.dto.FormUser;
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
        BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

//    Main 화면
    @GetMapping(value={"", "/"})
    public String home(){
        return "home";
    }

//    Login 처리
    @GetMapping(value="/login")
    public String loginPage(Model model){
        log.info("Call get login");
        return "securityLogin";
    }

//    *** login process 는 security에게 맡기기 때문에 필요x
//    @PostMapping(value="/login")
//    public String login(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult){
//        log.info("Call post login");
//        User loginUser = userService.login(formUser.toEntity());
//        if(loginUser != null){
//            return "home";
//        }else{
//            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다");
//            return "redirect:/security-login";
//        }
//    }

//    Join 처리
    @GetMapping(value="/create")
    public String createForm(Model model){
        model.addAttribute("formUser",new FormUser());
        return "createForm";
    }

//    과제: DB 에 name 중복 확인을 2번 하는 낭비 해결 필요
    @PostMapping(value="/create")
    public String createUser(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult){
        if(userService.checkNameDuplicate(formUser.getName())){
            bindingResult.addError(new FieldError("formUser","name","ID 중복"));
            return "createForm";
        }else{
            User user = formUser.toEntity();
            userService.join(user);
            return "redirect:/security-login";
        }
    }

//    Admin 페이지 처리 (security 에게 의존)
    @GetMapping(value="/admin")
    public String getAdminPage(){
        return "admin";
    }
}
