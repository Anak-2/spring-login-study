package user.study.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/form-login")
@RequiredArgsConstructor
@Slf4j
public class FormLoginController {

    private final UserService userService;

    @GetMapping(value={"", "/"})
    public String home(){
        return "home";
    }

    @GetMapping(value="/login")
    public String loginPage(){
        return "login";
    }

    @PostMapping(value="/login")
    public String login(@ModelAttribute FormUser formUser, Model model){
        log.info("input id: {} pwd: {}",formUser.getName(),formUser.getPwd());
        List<User> users = userService.login(formUser.getName(), formUser.getPwd());
        if(!users.isEmpty()) model.addAttribute("user",users.get(0));
        return "login";
    }

    @GetMapping(value="/create")
    public String createForm(Model model){
//        객체를 생성해서 crateForm.html 연결해줘야 thymeleaf로 객체 받을 수 있음
        model.addAttribute("formUser", new FormUser());
        return "createForm";
    }

    @PostMapping(value="/create")
    public String createUser(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult){
        log.info("Call createUser Post");
        log.info("sign id: {} pwd: {}",formUser.getName(),formUser.getPwd());
        if(userService.checkNameDuplicate(formUser.getName())){
            bindingResult.addError(new FieldError("formUser","name","ID 중복"));
        }
        if(bindingResult.hasErrors()){
            return "createForm";
        }
        userService.join(formUser);
        return "redirect:/form-login";
    }
}
