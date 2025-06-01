package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.TimeZone;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/1
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    // @Autowired
    // private ShopService shopService;

    @Autowired
    private RedisTemplate redisTemplate;

    // /** * 更新店铺状态
    //  * @param status 店铺状态
    //  * @return Result
    //  */
    // @PutMapping("/mysql/{status}")
    // @ApiOperation(value = "更新店铺状态", notes = "1: 营业, 0: 打烊")
    // public Result updateStatus(@PathVariable Short status){
    //     log.info("设置店铺状态为: {}", status == 1 ? "营业" : "打烊");
    //
    //     long start = System.currentTimeMillis();
    //     shopService.update(status);
    //     long end = System.currentTimeMillis();
    //     log.info("设置m店铺状态耗时: {} ms", end - start);
    //
    //     return Result.success();
    // }

    /**
     * 更新店铺状态到Redis
     * @param status 店铺状态
     * @return Result
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "更新店铺营业状态", notes = "1: 营业, 0: 打烊")
    public Result updateStatusToRedis(@PathVariable Short status) {
        log.info("设置店铺状态到Redis: {}", status == 1 ? "营业" : "打烊");

        // 更新Redis中的店铺状态
        redisTemplate.opsForValue().set(KEY, status);

        return Result.success();
    }

    // /**
    //  * 获取店铺状态
    //  * @return Result
    //  */
    // @GetMapping("/mysql/status")
    // @ApiOperation(value = "获取店铺状态", notes = "1: 营业, 0: 打烊")
    // public Result<Short> getShopMysqlStatus(){
    //     log.info("获取店铺状态");
    //     long start = System.currentTimeMillis();
    //     short status = shopService.getStatus();
    //     long end = System.currentTimeMillis();
    //     log.info("获取m店铺状态耗时: {} ms", end - start);
    //     return Result.success(status);
    // }

    /**
     * 获取Redis中的店铺状态
     * @return Result
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取Redis中的店铺营业状态", notes = "1: 营业, 0: 打烊")
    public Result<Short> getShopRedisStatus(){
        log.info("获取Redis中的店铺状态");
        Object o = redisTemplate.opsForValue().get(KEY);
        return Result.success((Short) o);
    }


}
