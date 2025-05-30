package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.exception.SetmealNotFoundException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/29
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO 套餐数据传输对象
     */
    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 保存套餐信息
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        // 批量插入套餐菜品
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.queryPage(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        PageHelper.clearPage();
        return new PageResult(total, records);
    }

    /**
     * 批量删除套餐
     *
     * @param ids 套餐ID列表
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        List<Setmeal> setmeals = setmealMapper.getByIds(ids);
        setmeals.forEach(setmeal -> {
            if (StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        setmealDishMapper.deleteBySetmealIds(ids);
        setmealMapper.deleteByIds(ids);
    }

    /**
     * 更新套餐信息
     *
     * @param setmealDTO 套餐信息
     */
    @Transactional
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Long id = setmealDTO.getId();
        if (setmealDTO.getSetmealDishes() != null && !setmealDTO.getSetmealDishes().isEmpty()) {
            // 如果套餐菜品列表不为空，先删除原有的套餐菜品
            setmealDishMapper.deleteBySetmealIds(List.of(id));
            List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
            // 设置套餐ID
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id));
            setmealDishMapper.insertBatch(setmealDishes);
        }
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 更新套餐信息
        setmealMapper.updateByIds(setmeal, List.of(setmeal.getId()));
    }

    /**
     * 更新套餐状态
     *
     * @param status 套餐视图对象
     */
    @Override
    public void switchStatus(SetmealVO status) {
        Long id = status.getId();
        List<Dish> setmealDishes = setmealDishMapper.getDishBySetmealId(id);
        setmealDishes.forEach(setmealDish -> {if (StatusConstant.DISABLE.equals(setmealDish.getStatus())) {
            throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }});
        setmealMapper.updateByIds(Setmeal.builder().status(status.getStatus()).build(), List.of(id));
    }

    /**
     * 根据ID查询套餐
     *
     * @param id 套餐ID
     * @return 套餐视图对象
     */
    @Override
    public SetmealVO getById(Long id) {
        log.info("根据id查询套餐: {}", id);
        SetmealVO setmealVO = setmealMapper.getById(id);
        if (setmealVO == null) {
            throw new SetmealNotFoundException(MessageConstant.SETMEAL_NOTFOUND_FAILED);
        }
        return setmealVO;
    }
}
