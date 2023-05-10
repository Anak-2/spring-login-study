package user.study.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import user.study.member.domain.dto.FormUser;
import user.study.member.domain.user.User;
import user.study.member.global.exception.NotAuthorizedException;
import user.study.member.service.UserService;
import user.study.member.service.UserServiceImpl;

import java.util.List;

@Controller
//@RequiredArgsConstructor
@RequestMapping(value = "/session-login")
@Slf4j
public class SessionLoginController {

    private final UserService userService;

    public SessionLoginController(@Qualifier("userServiceImpl") UserService userService){
        this.userService = userService;
    }

    @GetMapping(value = {"", "/"})
    public String home(@SessionAttribute(name = "userName", required = false) String userName, Model model){
//        Session 있으면 model 에 추가
        if (userName != null && !userName.equals("")) {
            User user = userService.getLoginUser(userName);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "home";
    }

    @GetMapping(value = "/login")
    public String loginPage(Model model) {
        model.addAttribute("formUser", new FormUser());
        return "login";
    }

    //    session 에 userName 정보 추가
    @PostMapping(value = "/login")
    public String login(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response) {
        List<User> users = userService.login(formUser.getName(), formUser.getPwd());

        if (users.isEmpty()) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다");
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach(objectError -> log.info("bindingResult Error: {}", objectError));
            return "login";
        }
        HttpSession session = request.getSession();
        log.info("sessionId : {}",session.getId());
        session.setAttribute("userName",formUser.getName());
        return "redirect:/session-login";
    }

    @GetMapping(value = "/create")
    public String createForm(Model model) {
        model.addAttribute("formUser", new FormUser());
        return "createForm";
    }

    @PostMapping(value = "/create")
    public String createUser(@Valid @ModelAttribute FormUser formUser, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (userService.checkNameDuplicate(formUser.getName())) {
            bindingResult.addError(new FieldError("formUser", "name", "ID 중복"));
        }
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().stream().forEach(objectError -> log.info("bindingResult Error: {}", objectError));
            return "createForm";
        }
        userService.join(formUser);
        /*
        redirect 했을 때 객체 넘겨주는 방법 (addAttribute, addFlashAttribute 차이)
        redirectAttributes.addAttribute("user",formUser.toEntity()); --> Url 로 넘겨주기 때문에 Controller 에서 RequestParam 으로 받아줘야한다
        .addFlashAttribute() : Post 방식과 유사하게 Url에 포함 안하고, 세션으로 넘겨주기 때문에 Controller 에서 따로 처리 필요 없이 html까지 넘어간다.
        단 1회성이기 때문에 넘겨준 뒤로 사라진다
        */
        redirectAttributes.addFlashAttribute("user",formUser.toEntity());
        System.out.println(formUser.toEntity());
        return "redirect:/session-login";
    }

    //    Session 삭제
    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
//        session 이 있든 없든 삭제
        request.getSession().invalidate();
        return "redirect:/session-login";
    }

    @GetMapping(value = "/admin")
    public String getAdminPage(@SessionAttribute(name="userName", required = false) String userName, HttpServletResponse response, Model model){

        User user = userService.getLoginUser(userName);
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
