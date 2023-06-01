package user.study.member.filter.jwtV2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import org.thymeleaf.spring6.context.SpringContextUtils;
import user.study.member.config.auth.PrincipalDetails2;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
public class JwtAuthorizationFilter2 extends BasicAuthenticationFilter {

//    @Value 어노테이션으로 설정 파일에서 값을 가져오려 했는데 Spring Security 설정에서 new JwtAuthorizationFilter로
//    객체를 새로 생성해서 어노테이션이 무시되는 문제 (스프링 빈은 IoC 로 Singleton 패턴이기 때문에 자기가 생성해둔 객체만 관리!!)
//    @Value("{jwt.access.header}")
    private static final String accessHeader = "Authorization";
//    @Value("{jwt.refresh.header}")
    private static final String refreshHeader = "Authorization_refresh";
    AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter2(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        String jwt = request.getHeader(accessHeader);
        System.out.println("Call JwtAuthorizationFilter2");
        System.out.println("jwt: "+jwt);
        if(jwt == null){
            filterChain.doFilter(request,response);
            return;
        }
        Authentication authentication = checkAccessToken(jwt);
        if(authentication == null){
//            *** RefreshToken 을 헤더에서 가져오는 방식 ***
//            String rJwt = request.getHeader(refreshHeader);
            
//            *** RefreshToken 을 쿠키에서 가져오는 방식 ***
            Cookie[] cookies = request.getCookies();
            String cookieName = "refreshToken";
            String rJwt = null;
            for(Cookie c : cookies){
                if(cookieName.equals(c.getName())){
                    rJwt = c.getValue();
                }
            }
//            ToDo: AccessToken 이 만료되어서 새로 발급했지만 클라이언트의 localstorage 는 변함이 없다!!
            if(rJwt != null){
                rJwt = URLDecoder.decode(rJwt,"UTF-8");
                log.debug("refreshToken: {}",rJwt);
                rJwt = rJwt.replace(JwtTokenProvider.BEARER_TYPE, "");
                jwt = jwt.replace(JwtTokenProvider.BEARER_TYPE, "");
                String accessToken = JwtTokenProvider.refreshAccessToken(rJwt, jwt);
                response.setHeader("Authorization",accessToken);
                if(accessToken != null){
                    accessToken = accessToken.replace(JwtTokenProvider.BEARER_TYPE, "");
                }else{
                    log.error("Refresh Token has problem");
                }
                authentication = JwtTokenProvider.getAuthentication(accessToken);
            }else{
//                ToDo: 클라이언트에게 Refresh Token 이 필요하거나 만료되었다는 오류 전송 구현
//                해결: throw new ResponseStatusException();
                log.debug("Require Refresh Token");
            }
        }
        if(authentication != null){
            log.debug("인증 완료");
            log.debug("인증 정보: {}",authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private Authentication checkAccessToken(String jwt){
        String accessJwt = jwt.replace(JwtTokenProvider.BEARER_TYPE, "");
        return JwtTokenProvider.getAuthentication(accessJwt);
    }
}
