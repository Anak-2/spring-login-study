package user.study.member.filter.jwtV1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.user.User;
import user.study.member.repository.UserJpaRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

// 시큐리티가 가진 filter 중 BasicAuthenticationFilter 라는 것이 있는데, 기존 httpBasic 요청할 때 처리해주는 필터
// 권한이 필요한 URL 을 요청했을 때 작동하는 필터이다
// ToDo: anyRequest().permitAll() 로 권한이 필요없어도 볼 수 있는 URL을 호출했을 때도 doFilterInternal이 실행된다
//      권한을 요구하는데 적절한 권한이 없거나 로그인 하지 않았을 경우 여러 필터를 거치다가
//      AccessDeniedHandler (Forbidden 에러(403) 인가(권한) 에러) 와 AuthenticationEntryPoint (Unauthorized 에러(401) 인증(로그인) 에러) 에서 예외를 발생시킨다
//      그러니까 custom Error Handler 를 만들어서 Security Config 에 추가해주자
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserJpaRepository userJpaRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserJpaRepository userJpaRepository) {
        super(authenticationManager);
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("Call JwtAuthorizationFilter");
        
//        사용자가 보낸 헤더에 Authorization 필드가 있는지 확인
        String jwtHeader = request.getHeader("Authorization");

//      정상적인 토큰이 있는지 확인
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")){
            System.out.println("invalid jwtHeader");
//            System.out.println(getBody(request)); // ToDo: OAuth 로그인 시 빈 문자열 출력...
            chain.doFilter(request, response);
        }else{
            // request에 넘어온 JWT 토큰을 검증
            String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX,"");

    //        JWT 토큰을 줄 때 설정한 정보를 이용해 정보를 가져와본다
            String name = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build()
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
    //                강제로 시큐리티의 세션에 접근해서 Authentication 객체를 저장 (토큰이 인증된 상태를 세션에 저장)
//                    왜냐하면 권한 검사는 세션에서 꺼내서 확인하기 때문 (추정)
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
    //        다시 FilterChain 설정에 합류하는 함수
            chain.doFilter(request, response);
        }
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}
