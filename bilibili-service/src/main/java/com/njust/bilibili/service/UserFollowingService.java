package com.njust.bilibili.service;


import com.njust.bilibili.dao.UserFollowingDao;
import com.njust.bilibili.domain.FollowingGroup;
import com.njust.bilibili.domain.JsonResponse;
import com.njust.bilibili.domain.UserFollowing;
import com.njust.bilibili.domain.UserInfo;
import com.njust.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;

import java.util.*;
import java.util.stream.Collectors;

import static com.njust.bilibili.domain.constant.UserConstant.USER_FOLLOWING_GROUP_ALL_NAME;
import static com.njust.bilibili.domain.constant.UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Transactional  //事务处理：因为包含两个操作，删除和添加，添加事务保证第一个操作失败时能够回滚
    public void addUserFollowings(UserFollowing userFollowing){
        Long groupId= userFollowing.getGroupId();
        if(groupId==null){
            //查询默认分组的id
            FollowingGroup followingGroup=followingGroupService.getByType(USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setId(followingGroup.getId());
        }else {
            FollowingGroup followingGroup=followingGroupService.getById(groupId);
            if(followingGroup==null)
                throw new ConditionException("关注的分组不存在");
        }
        //判断关注的人是否存在
        if(userService.getUserById(userFollowing.getFollowingId())==null){
            throw new ConditionException("关注的用户不存在");
        }
        //删除原有的关注关系，因为现在的分组可能不同
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(),userFollowing.getFollowingId());
        //添加新的关注关系
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }
    //获取用户完整的关注列表（按分组来）
    //先查出userId对应的所有关注信息，然后将关注的up主的个人信息放在对应的UserFollowing中，最后分组
    public List<FollowingGroup> getUserFollowings(Long userId){
        List<UserFollowing>list=userFollowingDao.getUserFollowings(userId); //查出了当前用户有哪些关注条目
        //set去重，查出关注的所有up主的个人信息
        Set<Long> followingIdSet=list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo>userInfoList=new ArrayList<>();
        if(!followingIdSet.isEmpty()){
            userInfoList=userService.getUserInfoByUserIds(followingIdSet);
        }
        for(UserFollowing userFollowing:list){
            for(UserInfo userInfo:userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserid())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //分组
        //查出当前用户有哪些关注分组
        List<FollowingGroup>groupList=followingGroupService.getByUserId(userId);
        FollowingGroup allGroup=new FollowingGroup();//汇总全部关注，提供给前端
        allGroup.setName(USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);    //全部关注下，所有up主的信息

        List<FollowingGroup>result=new ArrayList<>();
        result.add(allGroup);
        for(FollowingGroup group:groupList){
            List<UserInfo>infoList=new ArrayList<>();
            for(UserFollowing userFollowing:list){
                if(group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }

    //获取用户粉丝的用户信息
    public List<UserFollowing> getUserFans(Long userId){
        List<UserFollowing>fanList=userFollowingDao.getUserFans(userId);
        //把粉丝的id都抽取出来
        Set<Long>fanIdSet=fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());

        List<UserInfo>fansInfoList=new ArrayList<>();   //粉丝信息列表
        if(!fanIdSet.isEmpty()){
            fansInfoList=userService.getUserInfoByUserIds(fanIdSet);
        }
        //将id与用户信息映射
        Map<Long,UserInfo>mp=new HashMap<>();
        for(UserInfo ui:fansInfoList){
            mp.put(ui.getUserid(),ui);
        }
        for(UserFollowing uf:fanList){
            uf.setUserInfo(mp.get(uf.getUserId()));
        }
        List<UserFollowing>followingList=userFollowingDao.getUserFollowings(userId);    //我的关注信息列表
        Set<Long>myFollowings=new HashSet<>();
        for(UserFollowing uf:followingList){
            myFollowings.add(uf.getFollowingId());
        }
        //对比关注列表，和粉丝列表，查找互粉的人
        for(UserInfo uf:fansInfoList){
            if(myFollowings.contains(uf.getUserid())){  //如果粉丝id在我关注的up主的id集合内，那么是互粉的
                uf.setFollowed(true);
            }
        }
   /*     for(UserFollowing fan:fanList){
            for(UserInfo userInfo:fansInfoList){
                if(fan.getUserId().equals(userInfo.getUserid())){
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            //关注列表和粉丝列表对比
            for(UserFollowing following:followingList){
                if(following.getFollowingId().equals(fan.getUserId())){
                    fan.getUserInfo().setFollowed(true);    //互粉
                }
            }
        }*/
        return fanList;
    }

    //通过用户id，查询该用户的所有关注分组
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return userFollowingDao.getUserFollowingGroups(userId);
    }

    //将传入的list中，凡是已经被userId关注过的，修改其followed属性为true
    public void checkFollowingState(List<UserInfo> list,Long userId) {
        List<UserFollowing>userFollowings=userFollowingDao.getUserFollowings(userId);
        //把所有关注的用户放入Set
        Set<Long>followingSet=new HashSet<>();
        for(UserFollowing uf:userFollowings){
            followingSet.add(uf.getFollowingId());
        }
        for(UserInfo ui:list){
            if(followingSet.contains(ui.getUserid()))
                ui.setFollowed(true);   //默认是false
        }
    }
}


