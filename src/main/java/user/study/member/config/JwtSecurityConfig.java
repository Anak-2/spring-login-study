package user.study.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import user.study.member.domain.user.Role;
import user.study.member.filter.MyFilter1;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final CorsFilter corsFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("JwtSecurityFilterChain");
//        커스텀 필터는 spring security filterChain 전이나 후에 걸어야 오류가 안 난다
//        이 방식 또는 FilterConfig 의 FilterRegistrationBean 이용
//        http.addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class);
        http.csrf().disable();
//        disable the session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)// 모든 요청은 이 filter 를 거쳐서 오도록 설정
//                Controller에 @CrossOrigin 을 붙여주는 방법도 있지만 이 방식은 필터 추가와 다르게 인증이 필요 없는 url만 처리해줌
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeHttpRequests()
                .requestMatchers("/jwt-login/user/**")
                .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                .requestMatchers("/jwt-login/admin/**")
                .hasAnyRole(Role.ADMIN.name())
                .anyRequest().permitAll();
        return http.build();
    }
}
