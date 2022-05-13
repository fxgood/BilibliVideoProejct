package com.njust.bilibili.dao;
import com.njust.bilibili.domain.FollowingGroup;
import com.njust.bilibili.domain.JsonResponse;
import com.njust.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowingDao {

     Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

     Integer addUserFollowing(UserFollowing userFollowing);

     List<UserFollowing> getUserFollowings(Long userId);

     List<UserFollowing> getUserFans(Long userId);

     List<FollowingGroup> getUserFollowingGroups(Long userId);
}
