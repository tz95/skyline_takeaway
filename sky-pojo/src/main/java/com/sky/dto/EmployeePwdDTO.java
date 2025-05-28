package com.sky.dto;

import lombok.Data;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/5/27
 */
@Data
public class EmployeePwdDTO {
    private Long empId;
    private String oldPassword;
    private String newPassword;
}
