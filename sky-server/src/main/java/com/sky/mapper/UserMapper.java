package com.sky.mapper;

import com.sky.dto.UserStatisticsDTO;
import com.sky.entity.User;
import com.sky.vo.UserReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return User对象
     */
    @Select("SELECT * FROM `user` WHERE id = #{userId}")
    User getById(Long userId);

    // /**
    //  * 根据条件查询用户数量
    //  * @param map 查询条件
    //  * @return 用户数量列表
    //  */
    // List<UserStatisticsDTO> countByMap(Map map);

    /**
     * 通过Map条件查询用户数量
     * @param map 查询条件
     * @return 根据条件查询到的用户数量
     */
    Integer countByMap(Map map);
}
