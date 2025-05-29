package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ID列表查询对应的套餐ID列表
     *
     * @param ids 菜品ID列表
     * @return 套餐ID列表
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
