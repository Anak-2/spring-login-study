package user.study.member.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 설정들을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.authorizeHttpRequests() // URI별 권한 관리를 설정하는 옵션의 시작점, 이게 선언되어야 requestMatchers 옵션 사용 가능
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
//                      OAuth logout
                        .logout().logoutSuccessUrl("/")
                .and()
//                      OAuth login
                        .oauth2Login()
//                      login 성공 이후 사용자 정보를 가져온 상태
                        .userInfoEndpoint().userService(customOAuth2UserService);

        return http.build();
    }
}