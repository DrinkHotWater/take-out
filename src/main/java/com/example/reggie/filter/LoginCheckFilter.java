package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 创建自定义的过滤器loginCheckFilter，检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        // 定义放行路径数组：不需要拦截处理的请求路径
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg", // 发送验证码
                "/user/login",   // 移动端用户登录
                "/doc.html", // 以下4项均为swagger自动生成api文档相关路径
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs",
        };

        // 2、判断本次请求路径是否在放行路径数组中，若在则不需要处理，直接放行
        if(check(urls, requestURI)) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 3.1、判断后台用户登录状态，若已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null) {
            log.info("后台用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));

            // 在线程局部变量中传入登录用户的id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        // 3.2、判断移动端用户登录状态，若已登录，则直接放行
        if(request.getSession().getAttribute("user") != null) {
            log.info("移动端用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            // 在线程局部变量中传入登录用户的id
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        // 4、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据，页面跳转由前端实现
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN"))); // 前端校验字符为NOTLOGIN，不能更改
        return;

    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for(String url : urls) {
            if(PATH_MATCHER.match(url, requestURI)) { // 如果当前请求路径在放行路径数组中
                return true;
            }
        }
        return false;
    }
}
