package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/4
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 添加购物车
     * @param shoppingCart 购物车实体
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据ID更新购物车中商品数量
     * @param cart 购物车实体
     */
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumberById(ShoppingCart cart);

    /**
     * 插入新的购物车记录
     * @param shoppingCart 购物车实体
     */
    @Insert("INSERT INTO `shopping_cart` (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) " +
            "VALUES " +
            "(#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);
}
