package user.study.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
import user.study.member.filter.JwtAuthenticationFilter;
import user.study.member.filter.MyFilter1;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final CorsConfig corsConfig;

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
//        return authenticationConfiguration.getAuthenticationManager();
//    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.debug("JwtSecurityFilterChain");
//        커스텀 필터는 spring security filterChain 전이나 후에 걸어야 오류가 안 난다
//        이 방식 또는 FilterConfig 의 FilterRegistrationBean 이용
//        http.addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class);
        http.csrf().disable();
//        disable the session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .formLogin().disable()
                    .httpBasic().disable()
//                *** .addFilter(corsFilter) ***
//                *** .addFilter(new JwtAuthenticationFilter()) ***
//                --> 위 코드는 "Deprecate WebSecurityConfigurerAdapter" 때문에
//                AuthenticationManager 를 AbstractHttpConfigurer 에서 받아서 구현한 클래스를 apply 해주자
//                ToDo: 커스텀 필터 적용 참고 링크 https://github.com/spring-projects/spring-security/issues/10822
//                      https://www.zodaland.com/tip/63
                    .apply(new MyCustomDsl())
                .and()
                .authorizeHttpRequests()
                    .requestMatchers("/jwt-login/user/**")
                        .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                    .requestMatchers("/jwt-login/admin/**")
                        .hasAnyRole(Role.ADMIN.name())
                    .anyRequest().permitAll();
        return http.build();
    }

//    커스텀 필터 추가를 여기서 처리하기
    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            log.debug("Call MyCustomDsl");
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
//            모든 요청은 이 filter 를 거쳐서 오도록 설정, cors에 대해 허락하는 필터
//            Controller에 @CrossOrigin 을 붙여주는 방법도 있지만 이 방식은 필터 추가와 다르게 인증이 필요 없는 url만 처리해줌
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager)); // formLogin disable 한 것 활성화 시키는 필터
//                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
        }
    }
}
