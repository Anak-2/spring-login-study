package user.study.member.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import user.study.member.domain.user.Role;

// 일반 Spring Security 용 Config
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig1{
    
//    "localhost에서 리디렉션한 횟수가 너무 많습니다" 오류 조심

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("SecurityConfig1");
        http.csrf().disable();
//        항상 더 자세한 url 이 먼저 나오도록 작성해야 한다!!!!!!!!!!! (이전에 허용한 사용자가 접근 권한이 막힌 곳도 보는 오류 발생)
        http.authorizeHttpRequests()
                .requestMatchers("/security-login/admin/**").hasAnyRole(Role.USER.name())
//                .requestMatchers("/security-login/login","/security-login/createForm").permitAll()
//                .requestMatchers("/security-login/").permitAll()
//                .requestMatchers("/security-login").permitAll()
//                .requestMatchers("/error").permitAll()
                .anyRequest().permitAll();
//                .and()
//                .formLogin()
//                .loginPage("/security-login/login");
        return http.build();
    }
}
