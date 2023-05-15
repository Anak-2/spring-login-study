package user.study.member.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.logging.LogRecord;

// FilterRegistrationBean 에 등록할 Custom Filter
@Slf4j
public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("Call MyFilter1");
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String method = req.getMethod();
        String headerAuth = req.getHeader("Authorization");
        System.out.println(method);
        System.out.println(headerAuth);

        chain.doFilter(request, response); // Filter 재귀호출
    }
}
