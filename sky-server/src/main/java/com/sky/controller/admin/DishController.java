package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/28
 * * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDto 菜品信息
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增菜品")
    @CacheEvict(cacheNames = "dish", key = "#dishDto.categoryId")
    public Result save(@RequestBody DishDTO dishDto) {
        dishService.saveWithFlavor(dishDto);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品: {}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result delete(@RequestParam(value = "ids") List<Long> ids) {
        log.info("批量删除菜品: {}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("更新菜品")
    @CacheEvict(cacheNames = "dish", key = "#dishDto.categoryId")
    public Result update(@RequestBody DishDTO dishDto) {
        log.info("更新菜品: {}", dishDto);
        dishService.updateWithFlavor(dishDto);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品信息")
    public Result<DishVO> queryById(@PathVariable Long id) {
        log.info("根据ID查询菜品信息: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @GetMapping("/list")
    @ApiOperation("根据分类ID查询菜品列表")
    public Result<List<Dish>> queryByCategoryId(@RequestParam(value = "categoryId") Long categoryId) {
        log.info("根据分类ID查询菜品列表: {}", categoryId);
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("切换菜品状态")
    public Result switchStatus(@PathVariable(value = "status") int status,@RequestParam(value = "id") Long id) {
        log.info("切换菜品状态: {}, ID: {}", status, id);
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishService.updateStatusWithSetmeal(dish);
        return Result.success();
    }

}
