package com.njust.bilibili.api.support;

import com.njust.bilibili.domain.exception.ConditionException;
import com.njust.bilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component  //在项目构建时注入上下文中
public class UserSupport {
    public Long getCurrentUserId(){ //todo 这里有个问题，如果有多个用户在请求，怎么分辨
        //springboot提供的抓取请求上下文内容的方法
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if(userId < 0) {
            throw new ConditionException("非法用户");
        }
//        this.verifyRefreshToken(userId);
        return userId;
    }
}
