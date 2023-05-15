package user.study.member.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import user.study.member.filter.MyFilter1;

//@Configuration
//@Slf4j
//public class FilterConfig {

//    @Bean
//    public FilterRegistrationBean<MyFilter1> filter1(){
//        // ToDo: Filter 자동 등록될 때 아래 로그 안 찍히는 이유?
//        log.debug("Call FilterRegistrationBean");
//        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
//        bean.addUrlPatterns("/*"); // 어떤 url에서 필터를 적용할지 결정하는 메서드, "/**" 표현이 안 먹혀서 "/*" 사용
//        bean.setOrder(0); // 낮은 번호가 필터중에서 가장 먼저 실행됨, 이것은 0 우선순위로 실행된다는 의미
//        return bean;
//    }

//    @Bean
//    public FilterRegistrationBean<MyFilter1> filter2(){
//        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
//        bean.addUrlPatterns("/*");
//        bean.setOrder(1); // 이것은 1 우선순위로 실행된다는 의미
//        return bean;
//    }
//}
