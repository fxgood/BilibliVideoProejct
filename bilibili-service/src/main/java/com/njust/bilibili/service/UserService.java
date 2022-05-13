package com.njust.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.njust.bilibili.dao.UserDao;
import com.njust.bilibili.domain.PageResult;
import com.njust.bilibili.domain.User;
import com.njust.bilibili.domain.UserInfo;
import com.njust.bilibili.domain.constant.UserConstant;
import com.njust.bilibili.domain.exception.ConditionException;
import com.njust.bilibili.service.util.MD5Util;
import com.njust.bilibili.service.util.RSAUtil;
import com.njust.bilibili.service.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service    //标识是服务层的类，系统会自动注入，项目过程中可以直接调用，不用自己生成
public class UserService {
    @Autowired
    private UserDao userDao;

    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空!");
        }
        User dbUser = getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已经注册！");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawpassword;
        try {
            rawpassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败!");
        }
        if (StringUtils.isNullOrEmpty(rawpassword))
            throw new ConditionException("501", "密码为空");
        String md5password = MD5Util.sign(rawpassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5password);
        user.setCreateTime(now);
        userDao.addUser(user);
        //添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserid(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    //通过手机号和密码登录（获取用户token）
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        User dbUser;
        if (!StringUtils.isNullOrEmpty(phone)) {
            dbUser = userDao.getUserByPhone(phone);
        } else {
            String email = user.getEmail();
            if (StringUtils.isNullOrEmpty(email)) {
                throw new ConditionException("手机号和邮箱均为空");
            }
            dbUser = userDao.getUserByEmail(email);
        }
        if (dbUser == null) {
            throw new ConditionException("该用户不存在!");
        }
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            System.out.println("password=" + password);
            throw new ConditionException("无法解密!");
        }
        String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        if (user == null)  //我自己加的这句
            throw new ConditionException("没有该用户");
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserinfo(userInfo);
        return user;
    }

    public void updateUsers(User user) throws Exception {
        //查询是否有该用户，如果没有则抛出异常
        User dbUser = userDao.getUserById(user.getId());
        if (dbUser == null)
            throw new ConditionException("该用户不存在！");
        if (!StringUtils.isNullOrEmpty(user.getPhone())) {
            if (userDao.getUserByPhone(user.getPhone()) != null)
                throw new ConditionException("该手机号码已存在");
        }
        Date now = new Date();
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String decrypt = RSAUtil.decrypt(user.getPassword());
            //重新加盐  注意xml中的sql语句中也要判断salt是否为null或空，否则或造成密码没修改，但是salt修改了
            String salt = String.valueOf(now.getTime());
            user.setSalt(salt);
            String sign = MD5Util.sign(decrypt, salt, "UTF-8");
            user.setPassword(sign);
        }
        user.setUpdateTime(now);
        System.out.println(user);
        //更新用户信息
        userDao.updateUsers(user);
    }

    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }

    //通过nick模糊查询所有相关用户
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start", (no - 1) * size);    //开始的条目
        params.put("limit", size);
        Integer total = userDao.pageCountUserInfos(params);   //查询实际有多少匹配的总条目数
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.getListUserInfos(params);  //实际查出所有需要的userInfo

        }
        return new PageResult<>(total, list);

    }
}
