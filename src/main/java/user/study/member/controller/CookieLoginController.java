package user.study.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.global.exception.NotAuthorizedException;
import user.study.member.service.UserService;
import user.study.member.service.UserServiceImpl;

import java.util.List;

@Controller
@RequestMapping(value="/cookie-login")
//@RequiredArgsConstructor
@Slf4j
public class CookieLoginController {

    private final UserService userService;

    public CookieLoginController(@Qualifier("userServiceImpl") UserService userService){
        this.userService = userService;
    }

    @GetMapping(value={"", "/"})
    public String home(@CookieValue(name="userName", required = false) String userName, Model model){
//        쿠키 있으면 model 에 추가
        if(userName != null && !userName.equals("")){
            User user = userService.getLoginUser(userName);
            if(user != null){
                model.addAttribute("user", user);
            }
        }
        return "home";
    }

    @GetMapping(value="/login")
    public String loginPage(Model model){
        model.addAttribute("formUser", new FormUser());
        return "login";
    }

//    response 에 쿠키 정보 추가
    @PostMapping(value="/login")
    public String login(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult, Model model, HttpServletResponse response){
        Cookie loginCookie = new Cookie("userName", formUser.getName());
        List<User> users = userService.login(formUser.getName(), formUser.getPwd());

        if(users.isEmpty()){
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다");
        }
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().stream().forEach(objectError -> log.info("bindingResult Error: {}",objectError));
            return "login";
        }
        loginCookie.setMaxAge(60*60);
        response.addCookie(loginCookie);
        return "redirect:/cookie-login";
    }

    @GetMapping(value="/create")
    public String createForm(Model model){
        model.addAttribute("formUser", new FormUser());
        return "createForm";
    }

    @PostMapping(value="/create")
    public String createUser(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult){
        if(userService.checkNameDuplicate(formUser.getName())){
            bindingResult.addError(new FieldError("formUser","name","ID 중복"));
        }
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().stream().forEach(objectError -> log.info("bindingResult Error: {}",objectError));
            return "createForm";
        }
        userService.join(formUser);
        return "redirect:/cookie-login";
    }

//    쿠키 삭제
    @GetMapping(value = "/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("userName",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/cookie-login";
    }

    @GetMapping(value = "/admin")
    public String getAdminPage(@CookieValue(name="userName", required = false) Cookie cookie, HttpServletResponse response, Model model){

        User user = userService.getLoginUser(cookie.getValue());
        if(user == null){
            model.addAttribute("getAdminPageError","로그인이 필요합니다");
            throw new NotAuthorizedException("로그인이 필요합니다");
//            return "login";
        }else if(!user.getRole().getKey().equals("ROLE_GUEST")){
            model.addAttribute("getAdminPageError","관리 권한이 필요합니다");
            throw new NotAuthorizedException("관리 권한이 필요합니다");
//            return "home";
        }
        return "admin";
    }
}
