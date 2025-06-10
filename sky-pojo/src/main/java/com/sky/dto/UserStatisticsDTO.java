package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/9
 */
@Data
@Builder
@AllArgsConstructor
public class UserStatisticsDTO {

    private LocalDate date;

    private Integer total;

}
