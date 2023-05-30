package user.study.member.config.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.dto.response.UserResponseDto;
import user.study.member.filter.jwtV2.JwtTokenProvider;

import java.io.IOException;
import java.io.PrintWriter;

// OAuth2 로그인에 성공하면 JWT 를 생성해서 client 에게 전송
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("Call OAuth Authentication SuccessHandler");
        String targetUrl = "http://localhost:8080/jwt-login/home";
        if (response.isCommitted()) {
            this.logger.debug(LogMessage.format("Did not redirect to %s since response already committed.", targetUrl));
            return;
        }
        PrincipalDetails2 principalDetails2 = (PrincipalDetails2) authentication.getPrincipal();
        System.out.println("principalDetails2: "+principalDetails2.toEntity());
//        SecurityContextHolder.getContext().setAuthentication(authentication); // session : stateless 라 그런지 redirect 되면서 저장한 값이 사라지나보다
        UserResponseDto.TokenInfo token = JwtTokenProvider.generateToken(authentication);

//        Option 1. RestTemplate 이용해서 소셜 로그인을 한 사용자의 id 를 바디에 포함시켜서 ( /login , post 방식) 으로 보내주기
//        Option 2. Response 에 TokenInfo 추가하기 --> redirect 돼서 그런지 새 request에 response 에 담은 정보가 없다

//        response.setContentType("application/json");
//        PrintWriter writer = response.getWriter();
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonString = mapper.writeValueAsString(token);
//        writer.println(jsonString);
//        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
//        JSONObject jsonObject = new JSONObject();

//      ****  Option 3. redirect URI 에 Token 정보 포함시키기 ****
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("accessToken", token.getAccessToken())
                    .queryParam("refreshToken", token.getRefreshToken())
                    .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//                super.onAuthenticationSuccess(request, response, authentication);
    }

}
