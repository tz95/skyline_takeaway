package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/6
 */
@RestController(value = "adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 管理端订单查询
     * @param ordersPageQueryDTO 订单分页查询数据传输对象
     * @return 分页结果
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("条件查询订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("根据条件查询订单: {}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionOrdersQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 管理端订单数量统计
     * @return 各状态订单数量统计视图对象
     */
    @GetMapping("/statistics")
    @ApiOperation("各状态订单数量统计")
    public Result<OrderStatisticsVO> statisticsOrders(){
        log.info("统计各状态订单数量");
        OrderStatisticsVO orderStatisticsVO = orderService.getStatisticsOrders();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 管理端根据订单号查询订单
     * @param id 订单ID
     * @return 订单视图对象
     */
    @GetMapping("/details/{id}")
    @ApiOperation("根据订单ID查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable("id") Long id) {
        log.info("根据订单ID查询订单详情: {}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 管理端接单
     * @param ordersRejectionDTO 订单ID
     * @return 成功结果
     */
    @PutMapping(value = "/confirm")
    @ApiOperation("接单")
    public Result confirmOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("接单: {}", ordersRejectionDTO);
        orderService.confirmOrders(ordersRejectionDTO.getId());
        return Result.success();
    }

    /**
     * 管理端拒绝订单
     * @param rejectionDTO 拒单数据传输对象
     * @return 成功结果
     */
    @PutMapping(value = "/rejection", consumes = "application/json")
    @ApiOperation("拒单")
    public Result rejectOrder(@RequestBody OrdersRejectionDTO rejectionDTO) {
        log.info("拒单: {}", rejectionDTO);
        orderService.rejectionOrders(rejectionDTO);
        return Result.success();
    }

    /**
     * 管理端取消订单
     * @param cancelDTO 取消订单数据传输对象
     * @return 成功结果
     */
    @PutMapping(value = "/cancel", consumes = "application/json")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO cancelDTO) {
        log.info("取消订单: {}", cancelDTO);
        orderService.cancelOrder(cancelDTO);
        return Result.success();
    }

    /**
     * 管理端派送订单
     * @param id 订单ID
     * @return 成功结果
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id) {
        log.info("派送订单: {}", id);
        orderService.deliveryOrder(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result completeOrder(@PathVariable Long id){
        log.info("完成订单: {}", id);
        orderService.completeOrder(id);
        return Result.success();
    }

}
