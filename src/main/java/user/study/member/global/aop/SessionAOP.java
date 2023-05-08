package user.study.member.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class SessionAOP {
    /*
    * 1번째 pointcut은 클래스에 포함된 모든 메소드에 적용
    * 2번째 pointcut은 login 메소드에 적용
    * 개인적으로 custom annotation 을 만들어서 해당 annotation 이 붙은 메소드나 클래스에서 작동하는 AOP 가 좋아보인다
    * */
//    @AfterReturning(pointcut = "execution(* user.study.member.controller.SessionLoginController.*(..))", returning = "result")
//    @AfterReturning(pointcut = "execution(* user.study.member.controller.SessionLoginController.login(..))", returning = "result")
    public void printSessionId(JoinPoint joinPoint, Object result){
        HttpSession session = null;
        for(Object o : joinPoint.getArgs()){
            if(o instanceof HttpServletRequest){ // http request 일 때 Object 가져오기
                HttpServletRequest request = (HttpServletRequest) o;
                System.out.println(o);
                session = request.getSession();
    //        System.out.println("Session ID: " + session.getId());
    //        System.out.println("Session attribute 'user': " + session.getAttribute("user"));
            }
        }
    }
}
