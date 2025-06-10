package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/8
 */
public interface ReportService {


    /**
     * 获取营业额统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果视图对象
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 获取用户统计信息
     * @param begin 开始日期
     * @param end 结束日期
     * @return 用户统计结果视图对象
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 获取订单统计信息
     * @param begin 开始日期
     * @param end 结束日期
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);
}
