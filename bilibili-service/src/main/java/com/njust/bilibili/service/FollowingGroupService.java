package com.njust.bilibili.service;

import com.njust.bilibili.dao.FollowingGroupDao;
import com.njust.bilibili.domain.FollowingGroup;
import com.njust.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;

import static com.njust.bilibili.domain.constant.UserConstant.USER_FOLLOWING_GROUP_TYPE_USER;

@Service
public class FollowingGroupService {
    @Autowired
    private FollowingGroupDao followingGroupDao;

    //添加关注分组
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        //先查询这个分组是否已经存在
        List<FollowingGroup> dbFollowingGroups=followingGroupDao.getByUserId(followingGroup.getUserId());
        for(FollowingGroup f:dbFollowingGroups){
            if(f.getName().equals(followingGroup.getName()))
                throw new ConditionException("1001","该分组已经存在！");
        }
        //插入新的分组，并返回新分组的id
        followingGroup.setType(USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroup.setCreateTime(new Date());
        return followingGroupDao.addUserFollowingGroups(followingGroup);
    }

    //通过类型查询
    public FollowingGroup getByType(String type){
        return followingGroupDao.getByType(type);
    }

    //通过分组id查询
    public FollowingGroup getById(Long id){
        return followingGroupDao.getById(id);
    }

    //通过用户id查询其关注的分组
    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }
}
