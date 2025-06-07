package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/4
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端-购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车数据传输对象
     * @return 操作结果
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    @CacheEvict(cacheNames = "shoppingCartCache", key = "T(com.sky.context.BaseContext).getCurrentId()")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车，商品信息为: {}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询购物车列表")
    @Cacheable(cacheNames = "shoppingCartCache", key = "T(com.sky.context.BaseContext).getCurrentId()", unless = "#result.data == null || #result.data.size() == 0")
    public Result<List<ShoppingCart>> list(){
        log.info("查询购物车列表");
        List<ShoppingCart> result = shoppingCartService.showShoppingCart();
        return Result.success(result);
    }

    /**
     * 清空购物车
     *
     * @return 操作结果
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    @CacheEvict(cacheNames = "shoppingCartCache", key = "T(com.sky.context.BaseContext).getCurrentId()")
    public Result clean(){
        log.info("清空购物车");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 删除购物车中的商品
     *
     * @param shoppingCartDTO 购物车数据传输对象
     * @return 操作结果
     */
    @PostMapping("/sub")
    @ApiOperation("从购物车中删除商品")
    @CacheEvict(cacheNames = "shoppingCartCache", key = "T(com.sky.context.BaseContext).getCurrentId()")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车中的商品，商品信息为: {}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }

}
