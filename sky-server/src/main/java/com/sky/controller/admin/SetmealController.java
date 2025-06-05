package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/29
 */
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐: {}", setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("查询套餐列表")
    public Result<PageResult> getSetmealList(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("查询套餐列表: {}", setmealPageQueryDTO);
        PageResult page = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据ID查询套餐: {}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result delete(@RequestParam(value = "ids") Long[] ids) {
        log.info("批量删除套餐: {}", ids);
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的套餐");
        }
        // 调用服务层删除套餐
        setmealService.deleteByIds(Arrays.stream(ids).toList());
        return Result.success();
    }

    @PutMapping
    @ApiOperation("更新套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("更新套餐: {}", setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("更新套餐状态")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result switchStatus(@PathVariable(value = "status") Integer status,@RequestParam(value = "id") Long id) {
        log.info("更新套餐状态: status={}, id={}", status, id);
        setmealService.switchStatus(SetmealVO.builder().status(status).id(id).build());
        return Result.success();
    }

}
