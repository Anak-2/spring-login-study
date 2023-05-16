package user.study.member.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import user.study.member.config.oauth.PrincipalDetails2;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;

import java.io.IOException;
import java.util.Optional;

// 시큐리티가 가진 filter 중 BasicAuthenticationFilter 라는 것이 있는데,
// 권한이 필요한 URL 을 요청했을 때 작동하는 필터이다
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserJpaRepository userJpaRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserJpaRepository userJpaRepository) {
        super(authenticationManager);
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이 필요한 URL, Call JwtAuthorizationFilter");
        
//        사용자가 보낸 헤더에 Authorization 필드가 있는지 확인
        String jwtHeader = request.getHeader("Authorization");

//      정상적인 토큰이 있는지 확인
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")){
            chain.doFilter(request, response);
        }
        //    JWT 토큰을 검증
        String jwtToken = request.getHeader("Authorization").replace("Bearer ","");

//        JWT 토큰을 줄 때 설정한 정보를 이용해 정보를 가져와본다
        String name = JWT.require(Algorithm.HMAC512("cos")).build()
                .verify(jwtToken)
                .getClaim("name").asString();

//        만약 name이 있다면 서버에서 제공한 JWT 토큰이 맞다는 것 (인증이 제대로 된 것)
        if(name != null && !name.equals("")){
            Optional<User> user = userJpaRepository.findByName(name);
            if(user.isPresent()){
                PrincipalDetails2 principalDetails2 = new PrincipalDetails2(user.get());
//                AuthenticationManager 를 이용한 Authentication 객체를 만드는 것은 로그인이 정상적으로 실행됐을 때 만드는 방법
//                아래 방법은 강제로 Authentication 객체를 만드는 것, 이미 위에서 로그인 정보가 있는 유저인지 확인 했으므로
//                credentials 에 null 가능
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(principalDetails2, null, principalDetails2.getAuthorities());
//                강제로 시큐리티의 세션에 접근해서 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
//        다시 FilterChain 설정에 합류하는 함수
        chain.doFilter(request, response);
    }
}
