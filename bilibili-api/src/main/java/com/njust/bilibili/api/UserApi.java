package com.njust.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.njust.bilibili.api.support.UserSupport;
import com.njust.bilibili.domain.JsonResponse;
import com.njust.bilibili.domain.PageResult;
import com.njust.bilibili.domain.User;
import com.njust.bilibili.domain.UserInfo;
import com.njust.bilibili.service.UserFollowingService;
import com.njust.bilibili.service.UserService;
import com.njust.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //rest风格的控制器
public class UserApi {
    @Autowired  //自动注入
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    //todo 为什么可以返回自定义的数据类型JsonResponse，springboot如何将其解析为json返回给前端的？
    @GetMapping("/users")   //获取用户信息
    public JsonResponse<User>getUserInfo(){
        Long userId=userSupport.getCurrentUserId();
        User user=userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    //获取RSA公钥，让前端用户获取这个公钥
    @GetMapping("/rsa-pks")
    public JsonResponse<String>getRSAPublicKey(){
        String pk=RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    //用户注册
    @PostMapping("/users")
    public JsonResponse<String>addUser(@RequestBody User user){ //将前端传过来的json生成实体类User
        userService.addUser(user);
        return JsonResponse.success();  //在addUser中已经把异常都处理了，能走到这一步，说明成功
    }

    //登录（获取用户令牌）
    @PostMapping("/user-tokens")
    @CrossOrigin //解决跨域问题
    public JsonResponse<String>login(@RequestBody User user)throws Exception{
        String token=userService.login(user);
        return new JsonResponse<>(token);
    }

    //更新用户
    //实现对t_user表中的phone、email以及password三个字段的更新功能
    @PutMapping("/users") //用复数是Rest风格  put相比post具有幂等性
    public JsonResponse<String>updateUsers(@RequestBody User user) throws Exception {
        Long userid=userSupport.getCurrentUserId();
        user.setId(userid);
        userService.updateUsers(user);
        return JsonResponse.success();  //userService会处理异常，直接返回成功信息即可
    }

    //更新用户信息
    @PutMapping("/user-infos")
    public JsonResponse<String>updateUserInfos(@RequestBody UserInfo userInfo){
        Long userid=userSupport.getCurrentUserId(); //怎么做到区别不同用户的？我猜是用上下文context
        userInfo.setUserid(userid);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();  
    }

    //分页查询用户列表 (首页上方搜索框能够模糊搜索出一定的用户，方便我们进行关注和取消关注的操作）
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>>pageListUserInfos(@RequestParam Integer no/*当前的页码*/,@RequestParam Integer size/*分页大小*/,String nick/*实现模糊查询，可选，因此不加注解*/){
        Long userId=userSupport.getCurrentUserId();
        JSONObject params=new JSONObject(); /*阿里的fastJson，很好用*/
        params.put("no",no);
        params.put("size",size);
        params.put("nick",nick);
        params.put("userId",userId);
        PageResult<UserInfo>res=userService.pageListUserInfos(params);
        //在查询结果里，给当前用户关注的用户打上标记
        userFollowingService.checkFollowingState(res.getList(),userId);
        /*测试结果：？*/
        return new JsonResponse<>(res);
    }

}
