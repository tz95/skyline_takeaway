package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/6
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     *
     * @param orders 订单实体
     * @return 插入的订单ID
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单页对象查询订单列表
     * @param opq 订单分页查询数据传输对象
     * @return 订单列表
     */
    Page<OrderVO> getByPageQueryDTO(OrdersPageQueryDTO opq);

    /**
     * 根据订单ID查询订单详情
     * @param id 订单ID
     * @return 订单详情视图对象
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    OrderVO getById(Long id);

    /**
     * 获取各状态订单数量统计
     * @return 订单统计视图对象
     */
    OrderStatisticsVO getStatisticsOrders();
}
