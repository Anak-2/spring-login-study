package user.study.member.config.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import user.study.member.config.auth.PrincipalDetails2;

import java.io.IOException;

// OAuth2 로그인에 성공하면 JWT 를 생성해서 client 에게 전송
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("Call OAuth Authentication SuccessHandler");
        System.out.println("authentication.getClass: "+authentication.getClass());
        System.out.println("authentication.getName(): "+authentication.getName());
        System.out.println("authentication.getPrincipal().getClass: "+authentication.getPrincipal().getClass());
        PrincipalDetails2 principalDetails2 = (PrincipalDetails2) authentication.getPrincipal();
        System.out.println("principalDetails2: "+principalDetails2.toEntity());
        SecurityContextHolder.getContext().setAuthentication(authentication); // session : stateless 라 그런지 redirect 되면서 저장한 값이 사라지나보다
        getRedirectStrategy().sendRedirect(request, response, "/security-login");
        //        super.onAuthenticationSuccess(request, response, authentication);
    }

}
