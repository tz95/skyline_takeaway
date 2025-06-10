package com.sky.service.impl;

import com.sky.dto.SalesReportDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 报告服务实现类
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/8
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 获取营业额统计
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 营业额统计结果视图对象
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = getDateRange(begin, end);
        
        List<Double> turnoverList = new ArrayList<>();

        dateList.forEach(date->{
            // SELECT SUM(money) FROM `orders` WHERE DATE_FORMAT(order_time, '%Y-%m-%d') = #{date}
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status",Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = Optional.ofNullable(turnover).orElse(0.0);
            turnoverList.add(turnover);

        });
        // 封装返回结果
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
/*        HashMap hashMap = new HashMap(4);
        hashMap.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        hashMap.put("end", LocalDateTime.of(end, LocalTime.MAX));
        hashMap.put("status", Orders.COMPLETED);
        List<TurnoverStatisticsDTO> list = orderMapper.sumByDate(hashMap);
        // 获取营业额统计结果
        List<LocalDate> keys = new ArrayList<>(list.size());
        List<BigDecimal> values = new ArrayList<>(list.size());
        list.forEach(ts -> {
            keys.add(ts.getDate());
            values.add(ts.getAmount() != null ? ts.getAmount() : BigDecimal.ZERO);
        });
        // 封装返回结果
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(",", keys))
                .turnoverList(StringUtils.join(",", values))
                .build();*/
    }

    /**
     * 获取用户统计信息
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户统计结果视图对象
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end){
        List<LocalDate> dates = getDateRange(begin, end);
        // 获取指定日期范围内的用户统计数据
        List<Integer> totalList = new ArrayList<>();
        List<Integer> newList = new ArrayList<>();
        dates.forEach(date->{
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            totalList.add(userMapper.countByMap(map));
            map.put("begin", beginTime);
            newList.add(userMapper.countByMap(map));
        });
        // 构建并返回用户统计结果视图对象
        return UserReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .totalUserList(StringUtils.join(totalList,","))
                .newUserList(StringUtils.join(newList,","))
                .build();
    }
    /*@Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateRange = getDateRange(begin, end);
        // 将开始天数前移一天,使统计天数每天都能有新增数据
        List<UserStatisticsDTO> count =
                userMapper.countByMap(Map.of("begin", LocalDateTime.of(begin.plusDays(-1), LocalTime.MIN),
                        "end", LocalDateTime.of(end, LocalTime.MAX)));
        if (count.isEmpty()) {
            return buildUserReportVO(List.of(UserStatisticsDTO.builder().total(0).build()), dateRange);
        }
        // 判断获取到的开始日期是否在统计数据之前
        int lastValidTotal = 0,index = 0,size = count.size();
        if (!begin.isBefore(count.get(0).getDate()) && size == 1 || size > 1){
            UserStatisticsDTO us = count.get(index++);
            lastValidTotal = us.getTotal();
        }
        for (LocalDate date : dateRange) {
            UserStatisticsDTO us = count.get(index);
            if (us.getDate().equals(date)) {
                index++;
                lastValidTotal = us.getTotal();
                continue;
            }
            us.setDate(date);
            us.setTotal(lastValidTotal);
            count.add(index, us);
        }
        return buildUserReportVO(count, dateRange);
    }*/

    /**
     * 获取订单统计信息
     *
     * @param begin 开始日期
     * @param end   结束日期
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = getDateRange(begin, end);
        // 指定日期范围内的订单统计数据
        List<Integer> totalList = new ArrayList<>();
        // 指定日期范围内的有效订单统计数据
        List<Integer> validList = new ArrayList<>();
        dates.forEach(date->{
            // 查询当日订单总数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            // 查询当天有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            totalList.add(orderCount);
            validList.add(validOrderCount);
        });
        // 获取区间内的订单总数
        Integer totalOrderCount = totalList.stream().reduce(Integer::sum).get();
        // 获取区间内的有效订单总数
        Integer validOrderCount = validList.stream().reduce(Integer::sum).get();
        // 计算订单完成率
        Double completionRate = totalOrderCount == 0 ? 0.0 :  validOrderCount.doubleValue() / totalOrderCount;
        // 构建并返回订单统计结果视图对象
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .orderCountList(StringUtils.join(totalList,","))
                .validOrderCountList(StringUtils.join(validList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(completionRate)
                .build();
    }

    /**
     * 获取销售额前10的商品统计信息
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 销售额前10的商品统计结果视图对象
     */
    @Override
    public SalesTop10ReportVO getTop10Sales(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        // 获取指定日期范围内的销售额前10的商品统计数据
        List<SalesReportDTO> result = orderMapper.getTop10Sales(beginTime, endTime);
        List<String> names = result.stream().map(SalesReportDTO::getName).collect(Collectors.toList());
        List<String> counts = result.stream().map(SalesReportDTO::getSalesCount).collect(Collectors.toList());

        // 构建并返回销售额前10的商品统计结果视图对象
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names,","))
                .numberList(StringUtils.join(counts,","))
                .build();
    }

    /**
     * 导出运营数据报表
     *
     * @param resp 响应对象
     */
    @Override
    public void exportBusinessData(HttpServletResponse resp) {
        // 1、查询数据库，获取营业数据 --- 查询最近三十天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX));

        // 2、通过POI将数据写入到EXCEL中

        // 基于模板文件创建一个新的Excel文件
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx")) {
            XSSFWorkbook sheets = new XSSFWorkbook(is);
            // 填充数据
            XSSFSheet sheet = sheets.getSheetAt(0);
            sheet.getRow(1).getCell(1).setCellValue("时间: " + dateBegin + "至" + dateEnd);

            // 获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            // 获得第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            for (int i = 0; i < 30; i++){
                LocalDate date = dateBegin.plusDays(i);
                // 查询某日的营业数据
                businessData =
                        workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                // 获得某行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            ServletOutputStream os = resp.getOutputStream();
            sheets.write(os);
            sheets.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 3、通过输出流将EXCEL文件传输到客户端浏览器

    }

    /**
     * 获取指定日期范围内的订单数量
     *
     * @param begin  开始时间
     * @param end    结束时间
     * @param status 订单状态，null表示所有状态
     * @return 订单数量
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    /**
     * 获取指定日期范围内的日期列表
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 日期列表
     */
    private List<LocalDate> getDateRange(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin;
        do {
            dateList.add(current);
            current = current.plusDays(1);
        }while (!current.isAfter(end));
        return dateList;
    }

    // /**
    //  * 构建用户统计结果视图对象
    //  *
    //  * @param userStatistics 用户统计数据列表
    //  * @param dateRange      日期范围列表
    //  * @return 用户统计结果视图对象
    //  */
    // private UserReportVO buildUserReportVO(List<UserStatisticsDTO> userStatistics, List<LocalDate> dateRange) {
    //     StringBuilder dateSB = new StringBuilder();
    //     StringBuilder totalSB = new StringBuilder();
    //     StringBuilder newSB = new StringBuilder();
    //     if (userStatistics.size() != dateRange.size()) {
    //         UserStatisticsDTO us = userStatistics.get(0);
    //         // 如果统计数据的天数不等于日期范围的天数，说明有缺失数据
    //         for (LocalDate date : dateRange) {
    //             dateSB.append(date).append(",");
    //             totalSB.append(us.getTotal()).append(",");
    //             newSB.append(0).append(",");
    //         }
    //     }else {
    //         for (int i = 0; i < dateRange.size(); i++) {
    //             // 0并非统计数据的第一天，而是统计数据的第一天前一天
    //             if (i == 0){
    //                 continue;
    //             }
    //             LocalDate date = dateRange.get(i);
    //             UserStatisticsDTO us = userStatistics.get(i);
    //             dateSB.append(date).append(",");
    //             totalSB.append(us.getTotal()).append(",");
    //             newSB.append(us.getTotal() - userStatistics.get(i - 1).getTotal()).append(",");
    //         }
    //     }
    //     dateSB.deleteCharAt(dateSB.length() - 1);
    //     totalSB.deleteCharAt(totalSB.length() - 1);
    //     newSB.deleteCharAt(newSB.length() - 1);
    //     return new UserReportVO(dateSB.toString(), totalSB.toString(), newSB.toString());
    // }


}
