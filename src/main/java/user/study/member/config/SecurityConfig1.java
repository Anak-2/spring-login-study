package user.study.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import user.study.member.config.oauth.MyOAuth2UserService;
import user.study.member.domain.user.Role;

// 일반 Spring Security 용 Config
//@Configuration
//@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig1{
    
//    "localhost에서 리디렉션한 횟수가 너무 많습니다" 오류 조심

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
//    Configuration 을 생략할 때 Autowired 는 스프링 빈 컨테이너에 접근할 수 있어야 된다는 오류 발생
//    "Autowired members must be defined in valid Spring bean"
//    @Autowired
    private final MyOAuth2UserService myOAuth2UserService;

    private final AuthenticationFailureHandler customFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("SecurityConfig1");
        http.csrf().disable();
//        항상 더 자세한 url 이 먼저 나오도록 작성해야 한다!!!!!!!!!!! (이전에 허용한 사용자가 접근 권한이 막힌 곳도 보는 오류 발생)
        http.authorizeHttpRequests()
                .requestMatchers("/security-login/admin/**").hasAnyRole(Role.USER.name())
//                .requestMatchers("/","/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/security-login/login")
                .usernameParameter("name")
                .passwordParameter("pwd")
                /*
                *   과제: login 시 PrincipalDetailsService 를 이용하는 것 까진 도달했지만
                *   login 실패 뜸
                *   해결 : --> passwordParameter 가 디폴트로 password 인데 이것을 내 웹에서 <input type="password" name=pwd >로 받고있었기 때문에 발생한 문제!!!!
                 */
                .loginProcessingUrl("/security-login/login")
                .defaultSuccessUrl("/security-login")
                .failureHandler(customFailureHandler)
                .and()
                .logout()
                .logoutSuccessUrl("/security-login")
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/security-login")
                .failureHandler(customFailureHandler)
                .userInfoEndpoint()
                .userService(myOAuth2UserService);
        return http.build();
    }
}