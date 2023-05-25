package user.study.member.filter.jwtV2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import user.study.member.config.auth.PrincipalDetails2;
import user.study.member.domain.dto.response.UserResponseDto;
import user.study.member.domain.user.Role;
import user.study.member.domain.user.User;
import user.study.member.filter.jwtV1.JwtProperties;
import user.study.member.repository.UserJpaRepository;
import user.study.member.repository.UserRepository;
import user.study.member.service.UserJpaServiceImpl;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORITIES_KEY = "auth";
    public static final String BEARER_TYPE = "Bearer ";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 60*1000L; // 1m
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7*24*60*60*1000L; // 7d

//    properties 설정 파일에 설정한 암호키, static 으로 설정하면 Spring 이 처리 못해주니까 생성자에서 @Value 처리
    private static String secretKey;
    private static UserJpaRepository userJpaRepository;

    @Autowired
    public JwtTokenProvider(@Value("{jwt.secret}") String secretKey, UserJpaRepository userJpaRepository){
        JwtTokenProvider.secretKey = secretKey;
        JwtTokenProvider.userJpaRepository = userJpaRepository;
    }

//    Authentication(사용자 정보 인증된 객체) 를 가지고 AccessToken, RefreshToken 생성
    public static UserResponseDto.TokenInfo generateToken(Authentication authentication){
        System.out.println("secretKey: "+secretKey);
        String username = authentication.getName();
        String accessToken = generateAccessToken(username);
        String refreshToken = generateRefreshToken();

        return UserResponseDto.TokenInfo.builder()
                .accessToken(accessToken)
                .accessTokenExpirationTime(ACCESS_TOKEN_EXPIRE_TIME)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

//    AccessToken 생성
    public static String generateAccessToken(String username){
        return BEARER_TYPE + JWT.create()
                .withSubject("jwtStudy")
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .withClaim("username", username)
                .withClaim("type",TYPE_ACCESS)
                .sign(HMAC512(secretKey));
    }

//    RefreshToken 생성
    public static String generateRefreshToken(){
        return BEARER_TYPE + JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .withClaim("type",TYPE_REFRESH)
                .sign(HMAC512(secretKey));
    }

//    RefreshToken 이 유효하면 user 정보로 AccessToken 재발급, user 정보가 맞지 않거나 accessToken이 유효하지 않으면 null 반환
    public static String refreshAccessToken(String refreshToken, String accessToken){
        try{
            DecodedJWT rJwt = JWT.require(HMAC512(secretKey)).build()
                    .verify(refreshToken);
            DecodedJWT jwt = JWT.decode(accessToken);
            String username = jwt.getClaim("username").asString();
            return generateAccessToken(username);
        }
        catch(TokenExpiredException e){
            log.error("Refresh Token is Expired on "+e.getExpiredOn());
        }catch(SignatureVerificationException sve){
            log.error("Refresh Signature is invalidate");
        }
        return null;
    }

//    JWT 토큰을 검증한 뒤 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
//    @Param : 검증이 안된 accessToken 정보
//    @Return : 인증이 완료된 Authentication 객체 반환
    public static Authentication getAuthentication(String accessToken){
        log.debug("Call JwtTokenProvider.getAuthentication()");
        try{
            DecodedJWT jwt = JWT.require(HMAC512(secretKey)).build()
                    .verify(accessToken);
            String username = jwt.getClaim("username").asString();
            if (username != null && !username.equals("")) {
                User user = userJpaRepository.findByName(username).orElse(null);
                if(user != null){
                    PrincipalDetails2 principalDetails2 = new PrincipalDetails2(user);
                    return new UsernamePasswordAuthenticationToken(principalDetails2, null, principalDetails2.getAuthorities());
                }
            }
//            ToDo: userJpaRepository 를 Autowired 받아서 사용하려 했는데
//                static 메소드엔 static 변수만 사용할 수 있고, Autowired 변수는 static이 되면 안되기 때문에
//                  사용하지 못해서 막힘. 다른 방법 고안해보기
        }
//        verify 과정 중 발생하는 오류 잡아주기
        catch(TokenExpiredException e){
            log.error("Access Token is Expired on "+e.getExpiredOn());
        }catch(SignatureVerificationException sve){
            log.error("Access Signature is invalidate");
        }
        return null;
    }
}