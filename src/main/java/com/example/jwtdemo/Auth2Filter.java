package com.example.jwtdemo;

import com.example.jwtdemo.exception.KCException;
import com.example.jwtdemo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author penelope
 */
public class Auth2Filter extends FormAuthenticationFilter {


    @Resource
    private JwtUtil jwtUtils;
    private Logger logger = LoggerFactory.getLogger(Auth2Filter.class);

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        String token = getRequestToken((HttpServletRequest) request);
        try {
            //检查token有效性
            //ExpiredJwtException JWT已过期
            //SignatureException JWT可能被篡改
            jwtUtils.getClaimByToken(token);
        }catch (Exception e){
            onLoginFail(request,response);
            return false;
        }

        Long userId = getUserIdFromToken(token);
        request.setAttribute("USER_ID",userId);
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request,response)) {
            if (isLoginSubmission(request,response)) {
                return executeLogin(request,response);
            } else {
                return true;
            }
        }else {
            onLoginFail(request,response);
            return false;
        }
    }



    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String authToken = JwtToken.createToken();
        Cookie cookie = new Cookie("token", authToken);
        httpServletResponse.addCookie(cookie);
        return super.onLoginSuccess(token, subject, request, httpServletResponse);
    }


    /**
     * 登录失败调用事件
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token,
                                     AuthenticationException e, ServletRequest request, ServletResponse response) {
        String className = e.getClass().getName();
        String message;
        if (IncorrectCredentialsException.class.getName().equals(className)
                || UnknownAccountException.class.getName().equals(className)) {
            message = "用户或密码错误, 请重试.";
        } else if (e.getMessage() != null && StringUtils.startsWith(e.getMessage(), "msg:")) {
            message = StringUtils.replace(e.getMessage(), "msg:", "");
        } else {
            message = "系统出现点问题，请稍后再试！";
            e.printStackTrace(); // 输出到控制台
        }
        request.setAttribute(getFailureKeyAttribute(), className);
        request.setAttribute("msg", message);
        return true;
    }



//    *********************************************************


    private void onLoginFail(ServletRequest request, ServletResponse response){
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        request.setAttribute("msg","没有权限");
    }
    /**
     * 获取请求的token
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        //从header中获取token
        String token = httpRequest.getHeader(jwtUtils.getHeader());
        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            return httpRequest.getParameter(jwtUtils.getHeader());
        }
        if (StringUtils.isBlank(token)) {
            // 从 cookie 获取 token
            Cookie[] cookies = httpRequest.getCookies();
            if (null == cookies || cookies.length == 0) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtUtils.getHeader())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }

    /**
     * 根据 token 获取 userID
     *
     * @param token token
     * @return userId
     */
    private Long getUserIdFromToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new KCException("无效 token", HttpStatus.UNAUTHORIZED.value());
        }
        Claims claims = jwtUtils.getClaimByToken(token);
        if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
            throw new KCException(jwtUtils.getHeader() + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
        }
        return Long.parseLong(claims.getSubject());
    }

}
