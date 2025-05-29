package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
    @author tz95
    @project sky-take-out
    @date 2025/5/28
*/
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品和对应口味
     *
     * @param dishDto 菜品信息
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);

        // 向菜品表添加一条数据
        dishMapper.insert(dish);

        // 获取INSERT后生成的主键ID
        Long id = dish.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(df -> df.setDishId(id));
            // 向口味表添加n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO 分页查询条件
     * @return PageResult 分页结果
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品ID列表
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 判断菜品是否能被删除 -- 是否存在起售中的菜品？
        List<Dish> dishes = dishMapper.getByIds(ids);
        dishes.forEach(d -> {
            if (StatusConstant.ENABLE.equals(d.getStatus())) {
                throw new DeletionNotAllowedException("菜品 " + d.getName() + " 正在售卖中，无法删除");
            }
        });

        // 判断菜品是否能被删除 -- 是否被套餐关联？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 当前菜品有关联 不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的菜品数据
        dishMapper.deleteBatch(ids);
        // 删除菜品关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);


    }

    /**
     * 根据DishDTO更新菜品信息
     *
     * @param dishDto 菜品信息传输对象
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDto) {
        // 更新菜品信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);
        dishMapper.update(dish);
        // 删除菜品对应的口味信息
        dishFlavorMapper.deleteByDishIds(List.of(dish.getId()));
        // 重新插入菜品对应口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据ID查询菜品信息
     *
     * @param id 菜品ID
     * @return DishVO 菜品视图对象
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();
        // 查询菜品信息
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish, dishVO);
        // 查询菜品对应的口味信息
        dishVO.setFlavors(dishFlavorMapper.getByDishId(id));
        return dishVO;
    }

    /**
     * 根据分类ID列表批量查询菜品
     *
     * @param categoryId 分类ID
     * @return List<Dish> 菜品列表
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        return dishes;
    }

    /**
     * 根据传入的菜品信息更新菜品状态
     * @param dish 菜品信息
     */
    @Transactional
    @Override
    public void updateStatusWithSetmeal(Dish dish) {
        dishMapper.update(dish);
        // 获取当前菜品ID
        Long dishId = dish.getId();
        // 查询所有关联的套餐ID
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(List.of(dishId));
        if (setmealIds == null || setmealIds.isEmpty()) {
            // 如果没有关联的套餐ID，直接返回
            return;
        }
        Setmeal setmeal = Setmeal.builder().status(dish.getStatus()).build();
        // 更新所有关联套餐的状态
        setmealMapper.updateStatusByIds(setmeal, setmealIds);
    }
}
