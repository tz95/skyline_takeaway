package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/7
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 每分钟执行一次，处理超时未支付的订单
     * 订单状态为1（待支付）且订单时间小于当前时间15分钟的订单
     * SELECT * FROM `orders` WHERE status = 1 AND order_time < DATE_SUB(NOW(), INTERVAL 15 MINUTE);
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processOrderTimeout(){
        log.info("定时处理超时订单: {}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // SELECT * FROM `orders` WHERE status = 1 AND order_time < DATE_SUB(NOW(), INTERVAL 15 MINUTE);
        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);
        list.forEach(order -> {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("订单超时未支付，已自动取消");
            order.setCancelTime(LocalDateTime.now());
            orderMapper.update(order);
        });

    }

    /**
     * 每六小时执行一次，处理超时配送中订单
     */
    @Scheduled(cron = "0 0 6 * * ? ")
    public void processDeliveryOrder(){
        log.info("定时处理配送中订单: {}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusHours(3);

        List<Orders> list = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        list.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            orderMapper.update(order);
        });

    }

}
