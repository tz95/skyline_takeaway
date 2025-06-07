package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/4
 */
public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车数据传输对象
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车列表
     * @return 购物车列表
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 从购物车中删除商品
     * @param shoppingCartDTO 购物车数据传输对象
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
