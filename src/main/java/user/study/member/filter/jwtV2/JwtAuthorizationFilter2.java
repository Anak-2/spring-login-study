package user.study.member.filter.jwtV2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.context.SpringContextUtils;
import user.study.member.config.auth.PrincipalDetails2;

import java.io.IOException;

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
        if(jwt == null){
            filterChain.doFilter(request,response);
            return;
        }
        Authentication authentication = checkAccessToken(jwt);
        if(authentication == null){
            String rJwt = request.getHeader(refreshHeader);
            if(rJwt != null){
                rJwt = rJwt.replace(JwtTokenProvider.BEARER_TYPE, "");
                jwt = jwt.replace(JwtTokenProvider.BEARER_TYPE, "");
                String accessToken = JwtTokenProvider.refreshAccessToken(rJwt, jwt);
                if(accessToken != null){
                    accessToken = accessToken.replace(JwtTokenProvider.BEARER_TYPE, "");
                }
                authentication = JwtTokenProvider.getAuthentication(accessToken);
            }else{
                log.debug("Require Refresh Token");
            }
        }
        if(authentication != null){
            System.out.println(authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private Authentication checkAccessToken(String jwt){
        String accessJwt = jwt.replace(JwtTokenProvider.BEARER_TYPE, "");
        return JwtTokenProvider.getAuthentication(accessJwt);
    }
}
