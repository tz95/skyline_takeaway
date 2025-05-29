package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 向口味表添加n条数据
     *
     * @param flavors 口味信息列表
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品ID删除对应的口味列表
     *
     * @param ids 菜品ID
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品ID查询对应的口味列表
     *
     * @param id 菜品ID
     * @return List<DishFlavor> 口味列表
     */
    @Select("SELECT * FROM `dish_flavor` WHERE dish_id = #{id}")
    List<DishFlavor> getByDishId(Long id);
}
