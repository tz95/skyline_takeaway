package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/6
 */
public interface OrderService {


    /**
     * 用户提交订单
     * @param ordersSubmitDTO 订单提交数据传输对象
     * @return 订单提交结果视图对象
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO 订单分页查询数据传输对象
     * @return 分页结果
     */
    PageResult conditionOrdersQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单ID查询订单详情
     * @param id 订单ID
     * @return 订单详情视图对象
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 根据订单ID取消订单
     * @param id 订单ID
     */
    void cancelOrder(Long id);

    /**
     * 根据订单ID重新下单
     * @param id 订单ID
     */
    void repetitionOrder(Long id);

    /**
     * 统计各状态订单数量
     * @return 订单统计视图对象
     */
    OrderStatisticsVO getStatisticsOrders();

    /**
     * 接单
     * @param id 订单ID
     */
    void confirmOrders(Long id);

    /**
     * 拒单
     * @param rejectionDTO 订单拒绝数据传输对象
     */
    void rejectionOrders(OrdersRejectionDTO rejectionDTO);

    /**
     * 取消订单
     * @param cancelDTO 订单取消数据传输对象
     */
    void cancelOrder(OrdersCancelDTO cancelDTO);

    /**
     * 派送订单
     * @param id 订单ID
     */
    void deliveryOrder(Long id);

    /**
     * 完成订单
     * @param id 订单ID
     */
    void completeOrder(Long id);
}
