package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/4
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车数据传输对象
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 设置当前用户ID
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 查询新增到购物车的商品是否已经存在
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 如存在，数量+1;
        if (list!=null && !list.isEmpty()) {
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else {
            // 如不存在，插入一条新记录
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                // 设置菜品价格
                shoppingCart.setAmount(dish.getPrice());

            } else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                SetmealVO setmealVO = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setSetmealId(setmealId);
            }
            // 默认数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

        }


    }

    /**
     * 查询购物车列表
     *
     * @return 购物车列表
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> list = shoppingCartMapper.list(ShoppingCart.builder().userId(userId).build());
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 从购物车中删除商品
     *
     * @param shoppingCartDTO 购物车数据传输对象
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCartDTO.getDishId();
        List<ShoppingCart> list = shoppingCartMapper.list(ShoppingCart.builder()
                .userId(userId)
                .dishId(dishId)
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .setmealId(shoppingCartDTO.getSetmealId())
                .build());
        if (dishId != null || shoppingCartDTO.getSetmealId() != null) {
            if (list != null && !list.isEmpty()) {
                ShoppingCart cart = list.get(0);
                if (cart.getNumber() > 1) {
                    // 如果数量大于1，数量-1
                    cart.setNumber(cart.getNumber() - 1);
                    shoppingCartMapper.updateNumberById(cart);
                } else {
                    // 如果数量等于1，删除该记录
                    shoppingCartMapper.deleteByEntityId(cart.getId());
                }
            }
        }
    }

}
