package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/2
 */
@Mapper
public interface UserMapper {

    /**
     *  * 根据openid查询用户
     * @param openid 微信用户的唯一标识
     * @return User对象
     */
    @Select("SELECT * FROM `user` WHERE openid = #{openid}")
    User findByOpenid(String openid);

    /**
     * 插入用户
     * @param user 用户对象
     */
    void insert(User user);

}
