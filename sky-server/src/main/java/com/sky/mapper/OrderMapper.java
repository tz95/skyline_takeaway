package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.SalesReportDTO;
import com.sky.dto.TurnoverStatisticsDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据订单状态和订单时间查询订单
     * @param pendingPayment 订单状态
     * @param time 订单时间
     * @return 符合条件的订单列表
     */
    @Select("SELECT * FROM orders WHERE status = #{pendingPayment} AND order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime time);

    /**
     * 根据动态条件查询订单
     * @param map 包含查询条件的映射
     * @return 符合条件的订单列表
     */
    Double sumByMap(Map map);

    /**
     * 根据订单状态和订单时间查询订单
     * @param map 包含查询条件的映射
     * @return 符合条件的订单列表
     */
    List<TurnoverStatisticsDTO> sumByDate(Map map);

    /**
     * 获取订单数量
     * @return 订单数量
     */
    Integer countByMap(Map map);

    /**
     * 获取销售额前10的商品
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销售额前10的商品列表
     */
    List<SalesReportDTO> getTop10Sales(LocalDateTime begin, LocalDateTime end);
}
