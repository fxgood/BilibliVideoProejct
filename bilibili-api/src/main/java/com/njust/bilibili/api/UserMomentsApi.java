package com.njust.bilibili.api;


import com.njust.bilibili.api.support.UserSupport;
import com.njust.bilibili.domain.JsonResponse;
import com.njust.bilibili.domain.UserMoment;
import com.njust.bilibili.service.UserMomentsService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;    //用于获取用户id

    //添加动态
    @PostMapping("/user-moments")
    public JsonResponse<String>addUserMoments(@RequestBody UserMoment userMoment) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Long userId=userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    //获取用户订阅的所有动态
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>>getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment>list=userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
