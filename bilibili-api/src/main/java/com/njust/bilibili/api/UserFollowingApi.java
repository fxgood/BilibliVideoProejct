package com.njust.bilibili.api;


import com.njust.bilibili.api.support.UserSupport;
import com.njust.bilibili.domain.FollowingGroup;
import com.njust.bilibili.domain.JsonResponse;
import com.njust.bilibili.domain.UserFollowing;
import com.njust.bilibili.service.FollowingGroupService;
import com.njust.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//处理用户关注相关问题
@RestController
public class UserFollowingApi {
    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserSupport userSupport;    //用于获取当前登录用户的id
    @Autowired
    private FollowingGroupService followingGroupService;
    //添加用户关注
    @PostMapping("/user-followings")
    public JsonResponse<String>addUserFollowing(@RequestBody UserFollowing userFollowing){
        Long userId=userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    //获取用户关注列表
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>>getUserFollowings(){
        Long userId=userSupport.getCurrentUserId();
        List<FollowingGroup>res=userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(res);
    }



    //获取用户粉丝
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>>getUserFans(){
        Long userId=userSupport.getCurrentUserId();
        List<UserFollowing>res=userFollowingService.getUserFans(userId);
        return new JsonResponse<>(res);
    }

    //新建用户关注分组（返回新建分组的id）
    @PostMapping("/user-following-groups")
    public JsonResponse<Long>addUserFollowingGroups(@RequestBody FollowingGroup followingGroup){
        Long userId=userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        return new JsonResponse<>(followingGroupService.addUserFollowingGroups(followingGroup));
    }

    //获取用户关注分组
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>>getUserFollowingGroups(){
        Long userId=userSupport.getCurrentUserId();
        return new JsonResponse<>(userFollowingService.getUserFollowingGroups(userId));
    }

}
