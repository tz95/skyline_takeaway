package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDTO implements Serializable {
    private String name;
    private String salesCount;
}
