package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/1
 */
@Mapper
public interface ShopMapper {

    // /**
    //  * 更新店铺状态
    //  * @param status 店铺状态
    //  */
    // @Update("UPDATE `status` SET `status` = #{status} WHERE `id` = 1")
    // public void update(short status);
    //
    // /**
    //  * 获取店铺状态
    //  * @return 店铺状态
    //  */
    // @Select("SELECT s.`status` FROM `status` as s WHERE `id`=1")
    // short getStatus();
}
