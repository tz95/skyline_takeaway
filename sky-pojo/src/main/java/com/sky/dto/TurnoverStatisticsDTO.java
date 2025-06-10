package com.sky.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/8
 */
@Data
public class TurnoverStatisticsDTO implements Serializable {

    // 序列化版本号
    private static final long serialVersionUID = 1L;

    // 营业日期
    @NonNull
    private final LocalDate date;

    // 营业额
    @NonNull
    private final BigDecimal amount;



}
