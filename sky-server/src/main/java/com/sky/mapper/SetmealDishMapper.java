package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
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

    /**
     * 批量插入套餐菜品
     *
     * @param setmealDishes 套餐菜品列表
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐ID列表查询对应的菜品ID列表
     *
     * @param id 套餐ID列表
     * @return 菜品ID列表
     */
    List<SetmealDish> getBySetmealIds(List<Long> id);

    /**
     * 根据套餐ID查询对应的套餐菜品
     *
     * @param id 套餐ID
     * @return 套餐菜品列表
     */
    List<SetmealDish> getBySetmealId(Long id);


    /**
     * 根据套餐ID删除对应的套餐菜品
     *
     * @param ids 套餐ID列表
     */
    void deleteBySetmealIds(List<Long> ids);

    /**
     * 根据套餐ID查询对应的菜品列表
     *
     * @param id 套餐ID
     * @return 菜品列表
     */
    List<Dish> getDishBySetmealId(Long id);
}
