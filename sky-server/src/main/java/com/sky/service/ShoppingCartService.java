package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

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

}
