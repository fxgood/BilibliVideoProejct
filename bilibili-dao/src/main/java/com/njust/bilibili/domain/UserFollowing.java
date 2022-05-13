package com.njust.bilibili.domain;

import java.util.Date;
import java.util.List;

public class UserFollowing {
    private Long id;

    private Long userId;

    private Long followingId;   //被关注者的id

    private Long groupId;

    @Override
    public String toString() {
        return "UserFollowing{" +
                "id=" + id +
                ", userId=" + userId +
                ", followingId=" + followingId +
                ", groupId=" + groupId +
                ", createTime=" + createTime +
                ", userInfo=" + userInfo +
                '}';
    }

    private Date createTime;

    private UserInfo userInfo;


    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
