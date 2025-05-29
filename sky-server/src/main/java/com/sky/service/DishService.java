package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 */

public interface DishService {

    /**
     * 新增菜品和对应口味
     *
     * @param dishDto 菜品信息
     */
    void saveWithFlavor(DishDTO dishDto);

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO 分页查询条件
     * @return PageResult 分页结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     *
     * @param ids 菜品ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据DishDTO更新菜品信息
     * @param dishDto 菜品信息传输对象
     */
    void updateWithFlavor(DishDTO dishDto);

    /**
     * 根据ID查询菜品信息
     * @param id 菜品ID
     * @return DishVO 菜品视图对象
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据分类ID列表批量查询菜品
     * @param categoryId 分类ID
     * @return List<Dish> 菜品列表
     */
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 根据传入的菜品信息更新菜品状态
     * @param dish 菜品信息
     */
    void updateStatusWithSetmeal(Dish dish);
}
