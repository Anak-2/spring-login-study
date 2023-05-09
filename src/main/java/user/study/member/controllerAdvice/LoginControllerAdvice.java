package user.study.member.controllerAdvice;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import user.study.member.global.exception.NotAuthorizedException;

@ControllerAdvice // 모든 컨트롤러에 적용
@Slf4j
public class LoginControllerAdvice {

    @ModelAttribute // 모든 컨트롤러의 Model에 적용, login.html 재사용하기 위핸 코드
    public void handelRequest(HttpServletRequest request, Model model){
        String requestURI = request.getRequestURI();

        if(requestURI.contains("form-login")){
            model.addAttribute("loginType","form-login");
        }
        else if(requestURI.contains("cookie-login")){
            model.addAttribute("loginType","cookie-login");
        }
        else if(requestURI.contains("session-login")){
            model.addAttribute("loginType","session-login");
        }
        else if(requestURI.contains("security-login")){
            model.addAttribute("loginType","security-login");
        }
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseBody
    public ResponseEntity handleNotAuthorizedException(NotAuthorizedException notAuthorizedException){
        return new ResponseEntity(notAuthorizedException.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ServletException.class)
    @ResponseBody
    public ResponseEntity handleServletException(ServletException servletException){
        log.info("Call handleServletException");
        return new ResponseEntity(servletException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
