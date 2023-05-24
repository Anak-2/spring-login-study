package user.study.member.filter.jwtV1;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.user.User;
import com.auth0.jwt.JWT;

import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가
// formLogin 에서 id, pw 받았을 때 자동으로 작동한다.
// 하지만 JWT 를 사용하기 위해 loginFrom.disable() 했으므로
// addFilter 로 직접 추가해줘야한다
// ToDo: OAuth2.0 로그인 (소셜 로그인) 으로 로그인할 때 소셜 로그인의 name 으로 로그인 시 이상 작동
//      "Empty encoded password" --> 원인 파악 아직 못 함, UserDetailsService 의 loadUserByUsername() 까지 정상 작동
//  예측 문제: AuthenticationManager.authenticate() 메소드 호출했을 때 비밀번호를 BCrypt 이용한 비밀번호가 없어서 튕긴듯!
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

//    사용자의 정보를 인증(로그인)하는 것 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        log.debug("Call JwtAuthenticationFilter");
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        try {
            user = objectMapper.readValue(request.getInputStream(), User.class);

//            UsernamePasswordAuthenticationToken(Object principal, Object credentials)
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getName(), user.getPwd());
            log.debug("authenticationToken : {}",authenticationToken);
            
//            AuthenticationManager 에 Token 을 주면
//            PrincipalDetailsService 의 loadUserByUsername() 함수가 실행됨
//            그리고 로그인 한 정보가 Authentication 에 담김
//            그리고 넘겨준 Authentication 객체를 return 할 때 세션에 담아줌
//            jwt 쓰는데 세션에 담는 이유: 권한 처리를 위해!
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            PrincipalDetails2 principalDetails = (PrincipalDetails2) authentication.getPrincipal();

//            로그인이 잘 되면 다음 코드 정상 실행됨
            log.debug("로그인 정보 principalDetails.getUser().getName() : {}",principalDetails.getUser().getName());
            return authentication; // return 할 때 authentication 객체가 session 영역에 저장됨
//            ---------- successfulAuthentication 또는 unsuccessfulAuthentication 함수 실행
        } catch (IOException e) {
            log.debug("cannot find user");
            throw new RuntimeException(e);
        }
    }

//    사용자 정보가 인증되었을 때 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//       ToDo: 콘솔에 print는 되는데 log 는 출력이 안되는 때가 있네...? 뭐지
        log.debug("Call successfulAuthentication");
        System.out.println("Call successfulAuthentication");
        PrincipalDetails2 principalDetails2 = (PrincipalDetails2) authResult.getPrincipal();

//      사용자에게 넘겨줄 JWT 토큰 생성
        String jwtToken = JWT.create()
//                토큰 이름
                .withSubject(principalDetails2.getUsername())
//                토큰 유효기간 (60000 -> 1분)
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))
//                비공개 claim (넣고 싶은 key, value 값)
                .withClaim("id", principalDetails2.getUser().getId())
                .withClaim("name", principalDetails2.getUser().getName())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET)); // Token 암호화할 Key
//        응답하는 response 의 header 에 Authorization 정보 + 토큰 추가
        response.addHeader(JwtProperties.HEADER_STRING,JwtProperties.TOKEN_PREFIX+jwtToken);

//        아래 코드가 있으면 내가 만든 successfulAuthentication 이 덮어씌어져서 정보 사라짐
//        super.successfulAuthentication(request, response, chain, authResult);
    }

//    사용자 정보가 인증되지 않았을 때
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
    
}
