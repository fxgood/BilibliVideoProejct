package com.njust.bilibili.dao;

//用于跟数据库交互

import com.alibaba.fastjson.JSONObject;
import com.njust.bilibili.domain.User;
import com.njust.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper //mybatis的注解，与xml相关联，这里的方法会自动映射到mybatis的xml文件中
public interface UserDao {

    User getUserByPhone(String phone);

    Integer addUser(User user); //todo 为什么返回Integer类型

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUsers(User user);

    User getUserByEmail(String email);

    Integer updateUserInfos(UserInfo userInfo);

    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    Integer pageCountUserInfos(Map<String,Object> params);        //JSONObject实现了map接口

    List<UserInfo> getListUserInfos(JSONObject params);
}
