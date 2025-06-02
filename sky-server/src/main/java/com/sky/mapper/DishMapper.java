package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId 分类ID
     * @return 菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 向菜品表添加一条数据
     * @param dish 菜品信息
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 分页查询条件
     * @return Page<DishVO> 分页结果
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品信息
     * @param id 菜品ID
     * @return Dish 菜品信息
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 批量查找菜品
     * @param ids 菜品ID列表
     * @return List<Dish> 菜品列表
     */
    List<Dish> getByIds(List<Long> ids);

    /**
     * 批量删除菜品
     * @param ids 菜品ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据菜品信息更新菜品
     * @param dish 菜品信息
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类ID查询菜品列表
     * @param categoryId 分类ID
     * @return List<Dish> 菜品列表
     */
    @Select("SELECT * FROM `dish` WHERE category_id = #{categoryId}")
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 条件查询菜品
     * @param dish 菜品信息
     * @return List<Dish> 菜品对象列表
     */
    List<Dish> list(Dish dish);
}
