package com.njust.bilibili.service.handler;


import com.njust.bilibili.domain.JsonResponse;
import com.njust.bilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)  //设置最高优先级
public class CommonGlobalExceptionHandler {
    @ExceptionHandler(value=Exception.class)    //value=..表示只要抛出一个异常，就都要用它处理
    @ResponseBody   //返回参数是一个responsebody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request,Exception e){
        String errorMsg=e.getMessage();
        if(e instanceof ConditionException){
            //如果不是ConditionException类型，就返回报错信息
            String errorCode=((ConditionException) e).getCode();
            return new JsonResponse<>(errorCode,errorMsg);
        }else{
            //如果是ConditionException类型的，则返回状态码500
            return new JsonResponse<>("500",errorMsg);
        }
    }


}
