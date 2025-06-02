package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/1
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取Redis中的店铺状态
     * @return Result
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺营业状态", notes = "1: 营业, 0: 打烊")
    public Result<Short> getShopRedisStatus(){
        log.info("用户获取店铺状态");
        Object o = redisTemplate.opsForValue().get(KEY);
        return Result.success((Short) o);
    }


}
