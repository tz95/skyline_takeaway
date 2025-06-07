package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/6
 */
@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单详情
     *
     * @param orderDetailList 订单详情列表
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单ID查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情列表
     */
    List<OrderDetail> getByOrderId(Long id);
}
